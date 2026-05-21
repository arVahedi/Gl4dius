package io.gl4dius.cli.command;

import io.gl4dius.cli.service.SessionManagementService;
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
@CommandGroup(name = "Session Management", description = "session management commands")
public class SessionManagementCommands {

    private final SessionManagementService sessionManagementService;

    @Command(name = "session init", description = "Create a new session")
    public void createSession(
            @Option(longName = "name", shortName = 'n', description = "Name to session")
            String name,
            @Option(longName = "description", shortName = 'd', description = "Description of the session")
            String description) {
        this.sessionManagementService.createSession(name, description);
    }

    @Command(name = "session rm", description = "Delete a session")
    public String deleteSession(
            @Argument(index = 0, description = "ID or Name of the session to remove (default is current session)")
            String identifier) {
        var session = this.sessionManagementService.deleteSession(identifier);
        return "Deleted session %s (%s)".formatted(session.getName(), session.getId());
    }

    @Command(name = "session ls", description = "List all sessions")
    public Object listSessions() {
        return this.sessionManagementService.listSessions();
    }

    @Command(name = "session get", description = "Get a session")
    public Object getSession(
            @Argument(index = 0, description = "ID or Name of the session to remove (default is current session)")
            String identifier
    ) {
        return this.sessionManagementService.getSession(identifier);
    }

    @Command(name = "session edit", description = "Update current session")
    public Object updateSession(
            @Argument(index = 0, description = "ID or Name of the session to remove (default is current session)")
            String identifier,
            @Option(longName = "name", shortName = 'n', description = "Name to session")
            String name,
            @Option(longName = "description", shortName = 'd', description = "Description of the session")
            String description
    ) {
        return this.sessionManagementService.updateSession(identifier, name, description);
    }
}
