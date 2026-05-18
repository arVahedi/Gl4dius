package io.gl4dius.cli.utility;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.util.MacAddress;

import java.net.Inet4Address;
import java.net.InetAddress;
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
                .filter(NetInterfaceUtil::isUsableAddress)
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

    private static boolean isUsableAddress(@NonNull InetAddress address) {
        return !address.isAnyLocalAddress()
                && !address.isLoopbackAddress()
                && !address.isLinkLocalAddress()
                && !address.isMulticastAddress();
    }
}
