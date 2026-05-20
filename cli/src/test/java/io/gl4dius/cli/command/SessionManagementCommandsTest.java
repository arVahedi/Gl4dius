package io.gl4dius.cli.command;

import io.gl4dius.cli.fixture.SessionFixtures;
import io.gl4dius.cli.service.SessionManagementService;
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
class SessionManagementCommandsTest {

    @Mock
    private SessionManagementService sessionManagementService;

    @InjectMocks
    private SessionManagementCommands sessionManagementCommands;

    @Test
    void whenDeleteSession_thenDelegatesToServiceAndReturnsConfirmation() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionManagementService.deleteSession("demo")).thenReturn(session);

        var output = sessionManagementCommands.deleteSession("demo");

        assertThat(output)
                .contains("Deleted session demo")
                .contains(session.getId().toString());
    }

    @Test
    void whenListSessions_thenFormatsSessions() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionManagementService.listSessions()).thenReturn(List.of(session));

        var output = sessionManagementCommands.listSessions();

        assertThat(output)
                .contains("ID | Name | Mode | Description | Created At | Last Updated At")
                .contains(session.getId().toString())
                .contains("demo")
                .contains("-")
                .contains("test session");
    }

    @Test
    void whenListSessions_thenFormatsEmptyList() {
        when(sessionManagementService.listSessions()).thenReturn(List.of());

        assertThat(sessionManagementCommands.listSessions()).isEqualTo("No sessions found");
    }

    @Test
    void whenGetSession_thenFormatsSession() {
        var session = SessionFixtures.session(s -> s.setName("demo"));
        when(sessionManagementService.getSession("demo")).thenReturn(session);

        var output = sessionManagementCommands.getSession("demo");

        assertThat(output)
                .contains("ID: " + session.getId())
                .contains("Name: demo")
                .contains("Mode: -")
                .contains("Description: test session");
    }

    @Test
    void whenCreateSession_thenDelegatesToService() {
        sessionManagementCommands.createSession("demo", "test session");

        verify(sessionManagementService).createSession("demo", "test session");
    }
}
