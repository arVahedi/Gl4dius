package io.gl4dius.cli.utility;

import io.gl4dius.cli.model.dto.Ipv4Subnet;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.util.MacAddress;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

@UtilityClass
public class NetInterfaceUtil {

    public PcapNetworkInterface findInterface(String name) throws PcapNativeException {
        return Pcaps.getDevByName(name);  // e.g. "eth0", "en0"
    }

    public @NonNull Inet4Address resolveIp4(@NonNull PcapNetworkInterface nic) {
        List<Inet4Address> ipv4Addresses = nic.getAddresses().stream()
                .map(PcapAddress::getAddress)
                .filter(Inet4Address.class::isInstance)
                .map(Inet4Address.class::cast)
                .filter(Ipv4Util::isUsableAddress)
                .toList();

        if (ipv4Addresses.isEmpty()) {
            throw new IllegalArgumentException("No usable IPv4 address found for interface: %s"
                    .formatted(nic.getName()));
        }

        if (ipv4Addresses.size() > 1) {
            throw new IllegalStateException("Multiple usable IPv4 addresses found for interface %s: %s. Please select one explicitly."
                    .formatted(nic.getName(), ipv4Addresses));
        }

        return ipv4Addresses.getFirst();
    }

    public @NonNull MacAddress resolveMac(@NonNull PcapNetworkInterface nic) {
        // getLinkLayerAddresses() returns the interface's MACs
        return nic.getLinkLayerAddresses().stream()
                .filter(MacAddress.class::isInstance)
                .map(a -> (MacAddress) a)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No MAC on interface " + nic.getName()));
    }

    public Ipv4Subnet resolveIpv4Subnet(@NonNull String nicName) throws SocketException {
        NetworkInterface nic = NetworkInterface.getByName(nicName);
        List<InterfaceAddress> interfaceAddressList = nic.getInterfaceAddresses()
                .stream()
                .filter(interfaceAddress -> interfaceAddress.getAddress() instanceof Inet4Address)
                .toList();

        if (interfaceAddressList.size() > 1) {
            throw new IllegalStateException("Multiple IPv4 addresses found for interface %s: %s".formatted(nicName, interfaceAddressList));
        }

        if (interfaceAddressList.isEmpty()) {
            throw new IllegalStateException("No IPv4 address found for interface %s".formatted(nicName));
        }

        InterfaceAddress interfaceAddress = interfaceAddressList.getFirst();
        Inet4Address ipv4 = (Inet4Address) interfaceAddress.getAddress();
        short prefixLength = interfaceAddress.getNetworkPrefixLength();
        return Ipv4Subnet.from(ipv4, prefixLength);
    }
}
