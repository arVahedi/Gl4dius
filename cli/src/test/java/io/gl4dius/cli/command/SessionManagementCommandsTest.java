package io.gl4dius.cli.command;

import io.gl4dius.cli.fixture.SessionFixtures;
import io.gl4dius.cli.service.session.SessionManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void whenCreateSession_thenDelegatesToService() {
        sessionManagementCommands.createSession("demo", "test session");

        verify(sessionManagementService).createSession("demo", "test session");
    }
}
