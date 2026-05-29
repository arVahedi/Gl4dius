package io.gl4dius.cli.model.dto;

import io.gl4dius.cli.utility.Ipv4Util;
import org.jspecify.annotations.NonNull;

import java.net.Inet4Address;
import java.util.Iterator;
import java.util.NoSuchElementException;

public record Ipv4Subnet(
        int network,
        int broadcast
) {

    public static @NonNull Ipv4Subnet from(Inet4Address ip, short prefixLength) {
        int ipInt = Ipv4Util.ipv4ToInt(ip);
        int mask = Ipv4Util.prefixToMask(prefixLength);
        int network = ipInt & mask;
        int broadcast = network | ~mask;

        return new Ipv4Subnet(network, broadcast);
    }

    public @NonNull Iterable<String> hosts() {
        return () -> new Iterator<>() {
            private int current = network + 1;

            @Override
            public boolean hasNext() {
                return current < broadcast;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return Ipv4Util.intToIpv4(current++);
            }
        };
    }
}
