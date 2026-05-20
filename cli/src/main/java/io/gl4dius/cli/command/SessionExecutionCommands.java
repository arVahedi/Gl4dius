package io.gl4dius.cli.command;

import io.gl4dius.cli.service.SessionExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
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
            String identifier
    ) {
        this.sessionExecutionService.switchSession(identifier);
    }

    @Command(name = "session exit", description = "Exit from current session")
    public void switchSession() {
        this.sessionExecutionService.exitSession();
    }

    @Command(name = "session start", description = "Start current session")
    public void startSession() {
    }

    @Command(name = "session stop", description = "Stop current session")
    public void stopSession() {
    }
}
