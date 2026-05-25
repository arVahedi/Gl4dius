package io.gl4dius.cli.command;

import io.gl4dius.cli.service.session.SessionExecutionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Session Execution", description = "session execution commands")
public class SessionExecutionCommands {

    private final SessionExecutionService sessionExecutionService;

    @Command(name = "session switch", description = "Switch to another session")
    public void switchSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            @NotBlank
            String identifier
    ) {
        this.sessionExecutionService.switchSession(identifier);
    }

    @Command(name = "session exit", description = "Exit from current session")
    public void exitSession() {
        this.sessionExecutionService.exitSession();
    }

    @Command(name = "session start", description = "Start current session")
    public void startSession(
            @Option(longName = "interface", shortName = 'i', required = true, description = "NIC to poison")
            String interfaceName,
            @Option(longName = "target-ip", shortName = 't', description = "Target IP (if not specified, ALL clients in the LAN will be attacked)")
            String targetIp,
            @Option(longName = "target-mac", description = "Target MAC-Address")
            String targetMac,
            @Option(longName = "gateway", shortName = 'g', description = "Gateway IP")
            String gatewayIp
    ) {
        try {
            this.sessionExecutionService.startSession(interfaceName, targetIp, targetMac, gatewayIp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Command(name = "session stop", description = "Stop current session")
    public void stopSession() {
    }
}
