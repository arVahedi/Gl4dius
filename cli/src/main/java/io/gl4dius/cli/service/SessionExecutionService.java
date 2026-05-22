package io.gl4dius.cli.service;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.utility.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionExecutionService {

    private final DaemonModuleExecutor daemonModuleExecutor;
    private final SessionRepository sessionRepository;

    public void switchSession(String identifier) {
        var session = UuidUtil.parseUuidIfValid(identifier)
                .flatMap(this.sessionRepository::findById)
                .or(() -> this.sessionRepository.findByName(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Session %s not found".formatted(identifier)));

        exitSession();
        Gl4diusApplication.setCurrentSession(session);
        log.debug("Switched to session {}", session.getId());
    }

    public void exitSession() {
        Gl4diusApplication.getCurrentSession().ifPresent(session -> {
            log.debug("Exiting session {}", session.getId());
            stopSession();
        });
        Gl4diusApplication.setCurrentSession(null);
    }

    public void startSession() {

    }

    public void stopSession() {
        var session = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"));
        stopSession(session);
    }

    public void stopSession(@NonNull Session session) {
        log.debug("Stopping session {}", session.getId());
        this.daemonModuleExecutor.stop(session.getId());
    }
}
