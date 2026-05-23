package io.gl4dius.cli.service;

import io.gl4dius.cli.model.dto.system.CommandRequest;
import io.gl4dius.cli.model.dto.system.CommandResult;
import io.gl4dius.cli.module.arp.ArpSender;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetDiscoveryService {

    private static final int DEFAULT_ATTEMPTS = 5;
    private static final Duration DEFAULT_RETRY_DELAY = Duration.ofMillis(100);
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile("(?i)\\b([0-9a-f]{2}:){5}[0-9a-f]{2}\\b");

    private final SystemCommandExecutor commandExecutor;
    private final ArpSender arpSender;

    public Optional<String> findDefaultGateway(String nic) {
        CommandResult result = commandExecutor.execute(CommandRequest.of(
                Duration.ofSeconds(1),
                "ip", "route", "show", "default", "dev", nic
        ));

        if (!result.succeeded() || result.stdout().isBlank()) {
            return Optional.empty();
        }

        String[] parts = result.stdout().trim().split("\\s+");

        for (int i = 0; i < parts.length - 1; i++) {
            if ("via".equals(parts[i])) {
                return Optional.of(parts[i + 1]);
            }
        }

        return Optional.empty();
    }

    public Optional<String> resolveNeighborMacAddress(String nicName, String ip) throws Exception {
        var nic = NetInterfaceUtil.findInterface(nicName);
        var ownIP = NetInterfaceUtil.resolveIp4(nic);
        var ownMac = NetInterfaceUtil.resolveMac(nic);
        this.arpSender.sendArpRequest(nicName, ownIP.getHostAddress(), ownMac.toString(), ip);

        this.commandExecutor.execute(CommandRequest.of(
                Duration.ofSeconds(1),
                "ping", "-c", "1", ip
        ));

        for (int attempt = 0; attempt < DEFAULT_ATTEMPTS; attempt++) {
            Optional<String> macAddress = findNeighborMacAddress(nicName, ip);

            if (macAddress.isPresent()) {
                return macAddress;
            }

            Thread.sleep(DEFAULT_RETRY_DELAY);
        }

        return Optional.empty();
    }

    private Optional<String> findNeighborMacAddress(String nic, String ipAddress) {
        CommandResult result = commandExecutor.execute(CommandRequest.of(
                Duration.ofSeconds(1),
                "ip", "neigh", "show", ipAddress, "dev", nic
        ));

        log.debug("ip neigh result on nic {} for IP {} -> {}", nic, ipAddress, result.stdout());

        if (!result.succeeded() || result.stdout().isBlank()) {
            return Optional.empty();
        }

        return extractMacAddress(result.stdout());
    }

    private Optional<String> extractMacAddress(String output) {
        Matcher matcher = MAC_ADDRESS_PATTERN.matcher(output);
        if (matcher.find()) {
            return Optional.of(matcher.group().toLowerCase());
        }

        return Optional.empty();
    }
}
