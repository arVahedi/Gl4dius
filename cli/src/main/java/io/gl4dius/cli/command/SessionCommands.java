package io.gl4dius.cli.command;

import io.gl4dius.cli.assets.AttackMode;
import io.gl4dius.cli.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Session", description = "session management commands")
public class SessionCommands {

    private final SessionService sessionService;

    @Command(name = "session init", description = "Create a new session")
    public void createSession(
            @Option(longName = "name", shortName = 'n', description = "Name to session")
            String name,
            @Option(longName = "description", shortName = 'd', description = "Description of the session")
            String description,
            @Option(longName = "mode", shortName = 'm', description = "Attack mode")
            String mode
    ) {
        var attackMode = StringUtils.hasText(mode) ? AttackMode.fromString(mode) : null;
        this.sessionService.createSession(name, description, attackMode);
    }

    @Command(name = "session rm", description = "Delete a session")
    public void deleteSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            String identifier
    ) {

    }

    @Command(name = "session ls", description = "List all sessions")
    public void listSessions() {
    }

    @Command(name = "session get", description = "Get a session")
    public void getSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            String identifier
    ) {
    }

    @Command(name = "session update", description = "Update current session")
    public void updateSession() {
    }

    @Command(name = "session switch", description = "Switch to another session")
    public void switchSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            String identifier
    ) {
    }

    @Command(name = "session start", description = "Start current session")
    public void startSession() {
    }

    @Command(name = "session stop", description = "Stop current session")
    public void stopSession() {
    }
}
