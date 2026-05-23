package io.gl4dius.cli.service;

import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.service.session.SessionExecutionService;
import io.gl4dius.cli.service.session.SessionManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionManagementServiceTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionExecutionService sessionExecutionService;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    @Test
    void whenCreateSession_thenPersistsSession() {
        when(sessionRepository.findByName("demo")).thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            var session = invocation.getArgument(0, Session.class);
            session.setId(UUID.randomUUID());
            return session;
        });

        var session = sessionManagementService.createSession("demo", "test session");

        assertThat(session.getName()).isEqualTo("demo");
        assertThat(session.getDescription()).isEqualTo("test session");
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void whenCreateSession_thenRejectsDuplicateName() {
        when(sessionRepository.findByName("demo")).thenReturn(Optional.of(new Session()));

        assertThatThrownBy(() -> sessionManagementService.createSession("demo", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Session name demo already exists");

        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void whenCreateSessionAndNameIsBlank_thenUsesGeneratedIdAsName() {
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            var session = invocation.getArgument(0, Session.class);
            if (session.getId() == null) {
                session.setId(UUID.randomUUID());
            }
            return session;
        });

        var session = sessionManagementService.createSession(" ", null);

        assertThat(session.getId()).isNotNull();
        assertThat(session.getName()).isEqualTo(session.getId().toString());
    }

    @Test
    void whenGetSession_thenFindsSessionById() {
        var id = UUID.randomUUID();
        var expected = new Session();
        expected.setId(id);
        when(sessionRepository.findById(id)).thenReturn(Optional.of(expected));

        var session = sessionManagementService.getSession(id.toString());

        assertThat(session).isSameAs(expected);
        verify(sessionRepository, never()).findByName(id.toString());
    }

    @Test
    void whenGetSession_thenFindsSessionByName() {
        var expected = new Session();
        expected.setName("demo");
        when(sessionRepository.findByName("demo")).thenReturn(Optional.of(expected));

        var session = sessionManagementService.getSession("demo");

        assertThat(session).isSameAs(expected);
    }

    @Test
    void whenGetSession_thenRejectsMissingSession() {
        when(sessionRepository.findByName("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionManagementService.getSession("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Session missing not found");
    }

    @Test
    void whenDeleteSession_thenDeletesResolvedSession() {
        var session = new Session();
        session.setId(UUID.randomUUID());
        session.setName("demo");
        when(sessionRepository.findByName("demo")).thenReturn(Optional.of(session));

        var deleted = sessionManagementService.deleteSession("demo");

        assertThat(deleted).isSameAs(session);
        var captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).delete(captor.capture());
        assertThat(captor.getValue()).isSameAs(session);
    }

    @Test
    void whenListSessions_thenReturnsAllSessions() {
        var first = new Session();
        var second = new Session();
        when(sessionRepository.findAll()).thenReturn(List.of(first, second));

        assertThat(sessionManagementService.listSessions()).containsExactly(first, second);
    }
}
