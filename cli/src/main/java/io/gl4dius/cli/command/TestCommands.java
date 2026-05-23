package io.gl4dius.cli.command;

import io.gl4dius.cli.service.NetDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommands {

    private final ObjectMapper objectMapper;
    private final NetDiscoveryService netDiscoveryService;

    @Command(value = "test")
    public String test() throws Exception {
        log.info("Running test command");
        try {
            var gatewayIP = this.netDiscoveryService.findDefaultGateway("eth0").orElse("No default gateway found");
            var gatewayMac = this.netDiscoveryService.resolveNeighborMacAddress("eth0", gatewayIP).orElse("No gateway MAC found");
            var victimMac = this.netDiscoveryService.resolveNeighborMacAddress("eth0", "172.28.0.20").orElse("No victim MAC found");

            var result = Map.of(
                    "Gateway IP", gatewayIP,
                    "Gateway MAC", gatewayMac,
                    "Victim MAC", victimMac
            );

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            log.error("Error running test command", e);
            return "Error running test command";
        }
    }
}
