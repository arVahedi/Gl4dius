package io.gl4dius.cli.module.ipforwarding;

import io.gl4dius.cli.exception.Ipv4ForwardingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class Ipv4ForwardingManager {

    private static final Path IPV4_FORWARDING_PATH = Path.of("/proc/sys/net/ipv4/ip_forward");

    public void enableIpv4Forwarding() {
        if (isIpv4ForwardingEnabled()) {
            log.debug("IPv4 forwarding is already enabled");
            return;
        }
        writeIpv4ForwardingValue("1");
    }

    public void disableIpv4Forwarding() {
        if (!isIpv4ForwardingEnabled()) {
            log.debug("IPv4 forwarding is already disabled");
            return;
        }
        writeIpv4ForwardingValue("0");
    }

    public boolean isIpv4ForwardingEnabled() {
        try {
            String value = Files.readString(IPV4_FORWARDING_PATH).trim();
            return "1".equals(value);
        } catch (IOException e) {
            throw new Ipv4ForwardingException("Failed to read IPv4 forwarding status from %s"
                    .formatted(IPV4_FORWARDING_PATH), e);
        }
    }

    private void writeIpv4ForwardingValue(String value) {
        try {
            Files.writeString(IPV4_FORWARDING_PATH, value + System.lineSeparator());
        } catch (IOException e) {
            throw new Ipv4ForwardingException("Failed to write IPv4 forwarding value '%s' to %s. Are you running as root?"
                    .formatted(value, IPV4_FORWARDING_PATH), e);
        }
    }
}
