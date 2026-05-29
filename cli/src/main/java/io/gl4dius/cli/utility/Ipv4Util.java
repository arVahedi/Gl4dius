package io.gl4dius.cli.utility;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Ipv4Util {

    public static boolean isUsableAddress(@NonNull InetAddress address) {
        return !address.isAnyLocalAddress()
                && !address.isLoopbackAddress()
                && !address.isLinkLocalAddress()
                && !address.isMulticastAddress();
    }

    public static @NonNull List<String> generateIpv4Range(Inet4Address ip, short prefixLength) {
        int ipInt = ipv4ToInt(ip);
        int mask = prefixToMask(prefixLength);
        int network = ipInt & mask;
        int broadcast = network | ~mask;
        List<String> result = new ArrayList<>();

        for (int current = network + 1; current < broadcast; current++) {
            result.add(intToIpv4(current));
        }

        return result;
    }

    public int ipv4ToInt(@NonNull Inet4Address address) {
        byte[] bytes = address.getAddress();

        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }

    public int prefixToMask(short prefixLength) {
        return prefixLength == 0
                ? 0
                : (int) (0xFFFFFFFFL << (32 - prefixLength));
    }

    public @NonNull String intToIpv4(int value) {
        return String.format(
                "%d.%d.%d.%d",
                (value >>> 24) & 0xFF,
                (value >>> 16) & 0xFF,
                (value >>> 8) & 0xFF,
                value & 0xFF
        );
    }
}
