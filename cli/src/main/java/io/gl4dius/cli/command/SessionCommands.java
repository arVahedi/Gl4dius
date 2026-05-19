package io.gl4dius.cli.command;

import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Session", description = "session management commands")
public class SessionCommands {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneId.systemDefault());

    private final SessionService sessionService;

    @Command(name = "session init", description = "Create a new session")
    public void createSession(
            @Option(longName = "name", shortName = 'n', description = "Name to session")
            String name,
            @Option(longName = "description", shortName = 'd', description = "Description of the session")
            String description) {
        this.sessionService.createSession(name, description);
    }

    @Command(name = "session rm", description = "Delete a session")
    public String deleteSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            String identifier) {
        var session = this.sessionService.deleteSession(identifier);
        return "Deleted session %s (%s)".formatted(session.getName(), session.getId());
    }

    @Command(name = "session ls", description = "List all sessions")
    public String listSessions() {
        return this.formatSessions(this.sessionService.listSessions());
    }

    @Command(name = "session get", description = "Get a session")
    public String getSession(
            @Argument(index = 0, description = "ID or Name of the session to remove")
            String identifier
    ) {
        return this.formatSession(this.sessionService.getSession(identifier));
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

    private @NonNull String formatSessions(@NonNull List<Session> sessions) {
        if (sessions.isEmpty()) {
            return "No sessions found";
        }

        var output = new StringBuilder("ID | Name | Mode | Description | Created At | Last Updated At");
        for (Session session : sessions) {
            output.append(System.lineSeparator()).append(formatSessionLine(session));
        }
        return output.toString();
    }

    private @NonNull String formatSession(@NonNull Session session) {
        return """
                ID: %s
                Name: %s
                Mode: %s
                Description: %s
                Created At: %s
                Last Updated At: %s
                """.formatted(
                session.getId(),
                session.getName(),
                formatNullable(session.getConfig() != null ? session.getConfig().mode() : null),
                formatNullable(session.getDescription()),
                formatInstant(session.getCreatedAt()),
                formatInstant(session.getLastUpdateAt())
        ).stripTrailing();
    }

    private @NonNull String formatSessionLine(@NonNull Session session) {
        return "%s | %s | %s | %s | %s | %s".formatted(
                session.getId(),
                session.getName(),
                formatNullable(session.getConfig() != null ? session.getConfig().mode() : null),
                formatNullable(session.getDescription()),
                formatInstant(session.getCreatedAt()),
                formatInstant(session.getLastUpdateAt())
        );
    }

    private @NonNull String formatInstant(Instant instant) {
        return instant == null ? "-" : DATE_FORMATTER.format(instant);
    }

    private String formatNullable(Object value) {
        return value == null ? "-" : value.toString();
    }
}
