package io.gl4dius.cli.service;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.utility.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionExecutionService {

    private final SessionRepository sessionRepository;

    public void switchSession(String identifier) {
        var session = UuidUtil.parseUuidIfValid(identifier)
                .flatMap(this.sessionRepository::findById)
                .or(() -> this.sessionRepository.findByName(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Session %s not found".formatted(identifier)));

        Gl4diusApplication.setCurrentSession(session);
        log.info("Switched to session {}", session.getId());
    }

    public void exitSession() {
        //todo: do we need to stop session here?!
        Gl4diusApplication.setCurrentSession(null);
    }

    public void startSession() {

    }

    public void stopSession() {

    }
}
