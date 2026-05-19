package io.gl4dius.cli.command;

import io.gl4dius.cli.fixture.SessionFixtures;
import io.gl4dius.cli.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionCommandsTest {

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionCommands sessionCommands;

    @Test
    void whenDeleteSession_thenDelegatesToServiceAndReturnsConfirmation() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionService.deleteSession("demo")).thenReturn(session);

        var output = sessionCommands.deleteSession("demo");

        assertThat(output)
                .contains("Deleted session demo")
                .contains(session.getId().toString());
    }

    @Test
    void whenListSessions_thenFormatsSessions() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionService.listSessions()).thenReturn(List.of(session));

        var output = sessionCommands.listSessions();

        assertThat(output)
                .contains("ID | Name | Mode | Description | Created At | Last Updated At")
                .contains(session.getId().toString())
                .contains("demo")
                .contains("-")
                .contains("test session");
    }

    @Test
    void whenListSessions_thenFormatsEmptyList() {
        when(sessionService.listSessions()).thenReturn(List.of());

        assertThat(sessionCommands.listSessions()).isEqualTo("No sessions found");
    }

    @Test
    void whenGetSession_thenFormatsSession() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionService.getSession("demo")).thenReturn(session);

        var output = sessionCommands.getSession("demo");

        assertThat(output)
                .contains("ID: " + session.getId())
                .contains("Name: demo")
                .contains("Mode: -")
                .contains("Description: test session");
    }

    @Test
    void whenCreateSession_thenDelegatesToService() {
        sessionCommands.createSession("demo", "test session");

        verify(sessionService).createSession("demo", "test session");
    }
}
