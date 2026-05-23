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
            @Option(longName = "mode", shortName = 'm', defaultValue = "BC", description = "Poisoning mode: BC=BROADCAST, TS=TARGET_SPECIFIC")
            String mode,
            @Option(longName = "target-ip", shortName = 't', description = "Target IP (only used in TS mode)")
            String targetIp,
            @Option(longName = "target-mac", description = "Target MAC-Address (only used in TS mode)")
            String targetMac,
            @Option(longName = "gateway", shortName = 'g', required = true, description = "Gateway IP")
            String gatewayIp
    ) {

    }

    @Command(name = "session stop", description = "Stop current session")
    public void stopSession() {
    }
}
