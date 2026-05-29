package io.gl4dius.cli.module.arp;

import io.gl4dius.cli.model.dto.Ipv4Subnet;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArpSender {
    private static final int SNAP_LEN = 65536;
    private static final int TIMEOUT_MS = 10;

    private static final MacAddress UNKNOWN_MAC = MacAddress.getByName("00:00:00:00:00:00");

    /**
     * Sends a unicast ARP reply directly to a specific target.
     *
     * @param networkInterface local interface to send from (e.g. "eth0")
     * @param senderIp         the IP you are claiming to own in the reply
     * @param senderMacAddress the MAC you want the target to cache for senderIp
     * @param targetIp         the IP of the host whose cache you're updating
     * @param targetMacAddress the known MAC of that target host
     */
    public void sendArpReply(String networkInterface,
                             String senderIp, String senderMacAddress,
                             String targetIp, String targetMacAddress) throws Exception {

        PcapNetworkInterface nif = NetInterfaceUtil.findInterface(networkInterface);

        try (PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT_MS)) {

            MacAddress senderMac = MacAddress.getByName(senderMacAddress);
            MacAddress targetMac = MacAddress.getByName(targetMacAddress);
            Inet4Address senderAddr = (Inet4Address) InetAddress.getByName(senderIp);
            Inet4Address targetAddr = (Inet4Address) InetAddress.getByName(targetIp);

            var packet = buildArpFrame(ArpOperation.REPLY, targetMac,
                    senderMac, senderAddr,
                    targetMac, targetAddr);

            handle.sendPacket(packet);
            log.debug("ARP reply sent: {}({}) -> {}({})", senderIp, senderMac, targetIp, targetMac);
        }
    }

    public void sendBulkArpReply(String networkInterface,
                                 @NonNull Ipv4Subnet ipv4Subnet, String senderMacAddress,
                                 String targetIp, String targetMacAddress) throws Exception {

        PcapNetworkInterface nif = NetInterfaceUtil.findInterface(networkInterface);
        Inet4Address targetAddr = (Inet4Address) InetAddress.getByName(targetIp);
        MacAddress senderMac = MacAddress.getByName(senderMacAddress);
        MacAddress targetMac = MacAddress.getByName(targetMacAddress);

        int sent = 0;
        try (PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT_MS)) {
            for (var senderIp : ipv4Subnet.hosts()) {
                if (targetIp.equals(senderIp)) {
                    continue;
                }

                Inet4Address senderAddr = (Inet4Address) InetAddress.getByName(senderIp);

                var packet = buildArpFrame(ArpOperation.REPLY, targetMac,
                        senderMac, senderAddr,
                        targetMac, targetAddr);

                handle.sendPacket(packet);
                log.debug("ARP reply sent: {}({}) -> {}({})", senderIp, senderMac, targetIp, targetMac);

                sent++;

                if (sent % 100 == 0) {
                    log.debug("Sent {} ARP replies", sent);
                    LockSupport.parkNanos(1_000_000);
                }
            }
        }
    }

    /**
     * Sends a gratuitous ARP reply (announcement) on the given interface.
     * <p>
     * A gratuitous ARP is an unsolicited ARP reply broadcast to all devices on a local network,
     * used to proactively announce an IP-to-MAC mapping — for example, after a failover or IP reassignment.
     * All RFC-compliant hosts receiving it will update their ARP cache with the advertised mapping.
     * <p>
     * Per RFC 5227, this is sent as an ARP reply (not a request) with both the Ethernet
     * and ARP destination set to broadcast, ensuring maximum compatibility.
     *
     * @param networkInterface local interface to send from (e.g. "eth0")
     * @param senderIp         the IP you are claiming to own in the reply
     * @param senderMacAddress the MAC you want the target to cache for senderIp
     */
    public void sendGratuitousArp(String networkInterface,
                                  String senderIp, String senderMacAddress) throws Exception {
        PcapNetworkInterface nif = NetInterfaceUtil.findInterface(networkInterface);

        try (PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT_MS)) {

            MacAddress srcMac = MacAddress.getByName(senderMacAddress);
            Inet4Address srcIp = (Inet4Address) InetAddress.getByName(senderIp);

            var packet = buildArpFrame(ArpOperation.REPLY, MacAddress.ETHER_BROADCAST_ADDRESS,
                    srcMac, srcIp,
                    MacAddress.ETHER_BROADCAST_ADDRESS, srcIp);
            handle.sendPacket(packet);

            log.debug("Gratuitous ARP sent: ip={} mac={}", senderIp, srcMac);
        }
    }

    /**
     * Sends a gratuitous ARP request on the given interface.
     * <p>
     * Unlike a gratuitous ARP reply ({@link #sendGratuitousArp}), this is sent as a REQUEST
     * with the ARP target MAC zeroed out, signaling "I own this IP — is anyone else using it?"
     * <p>
     * Most hosts will still update their ARP cache upon receiving this (lenient implementations),
     * but it is not RFC 5227 compliant for cache-update purposes. Prefer {@link #sendGratuitousArp}
     * for announcements; use this for IP conflict detection (ACD) probing.
     *
     * @param networkInterface local interface to send from (e.g. "eth0")
     * @param senderIp         the IP you are claiming to own in the reply
     * @param senderMacAddress the MAC you want the target to cache for senderIp
     */
    public void sendGratuitousArpRequest(String networkInterface,
                                         String senderIp, String senderMacAddress) throws Exception {
        PcapNetworkInterface nif = NetInterfaceUtil.findInterface(networkInterface);

        try (PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT_MS)) {

            MacAddress srcMac = MacAddress.getByName(senderMacAddress);
            Inet4Address srcIp = (Inet4Address) InetAddress.getByName(senderIp);

            EthernetPacket packet = buildArpFrame(
                    ArpOperation.REQUEST,
                    MacAddress.ETHER_BROADCAST_ADDRESS,   // ethernet: flood to all
                    srcMac, srcIp,
                    UNKNOWN_MAC,                          // arp: no specific target MAC
                    srcIp                                 // arp: target IP = own IP (gratuitous)
            );

            handle.sendPacket(packet);
            log.debug("Gratuitous ARP request sent: ip={} mac={}", senderIp, srcMac);
        }
    }

    /**
     * Sends an ARP request asking who owns targetIp.
     * <p>
     * An ARP (Address Resolution Protocol) request is a network broadcast asking:
     * "Who has this IP address? Please tell me your MAC address."
     *
     * @param networkInterface local interface to send from (e.g. "eth0")
     * @param senderIp         the IP you are claiming to own in the reply
     * @param senderMacAddress the MAC you want the target to cache for senderIp
     * @param targetIp         the IP of the host whose cache you're updating
     */
    public void sendArpRequest(String networkInterface, String senderIp, String senderMacAddress,
                               String targetIp) throws Exception {
        PcapNetworkInterface nif = NetInterfaceUtil.findInterface(networkInterface);

        try (PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT_MS)) {

            MacAddress srcMac = MacAddress.getByName(senderMacAddress);
            Inet4Address senderAddr = (Inet4Address) InetAddress.getByName(senderIp);
            Inet4Address targetAddr = (Inet4Address) InetAddress.getByName(targetIp);


            var packet = buildArpFrame(
                    ArpOperation.REQUEST, MacAddress.ETHER_BROADCAST_ADDRESS,
                    srcMac, senderAddr,
                    UNKNOWN_MAC, targetAddr);
            handle.sendPacket(packet);

            log.debug("ARP request sent: who has {}? tell {}", targetIp, senderIp);
        }
    }

    private @NonNull EthernetPacket buildArpFrame(ArpOperation operation, MacAddress ethernetDstMac,
                                                  MacAddress senderMac, Inet4Address senderIp,
                                                  MacAddress targetMac, Inet4Address targetIp) {
        ArpPacket.Builder arpBuilder = new ArpPacket.Builder()
                .hardwareType(ArpHardwareType.ETHERNET)
                .protocolType(EtherType.IPV4)
                .hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
                .protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
                .operation(operation)
                .srcHardwareAddr(senderMac)
                .srcProtocolAddr(senderIp)
                .dstHardwareAddr(targetMac)
                .dstProtocolAddr(targetIp);

        return new EthernetPacket.Builder()
                .dstAddr(ethernetDstMac)
                .srcAddr(senderMac)
                .type(EtherType.ARP)
                .payloadBuilder(arpBuilder)
                .paddingAtBuild(true)
                .build();
    }
}
