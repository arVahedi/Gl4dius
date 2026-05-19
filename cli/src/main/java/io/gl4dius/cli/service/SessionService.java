package io.gl4dius.cli.service;

import io.gl4dius.cli.assets.AttackMode;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;


    public Session createSession(String name, String description, AttackMode mode) {
        if (StringUtils.hasText(name) && this.sessionRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Session name %s already exists".formatted(name));
        }

        var session = new Session();
        session.setName(name);
        session.setDescription(description);
        session.setMode(mode);

        session = this.sessionRepository.save(session);

        log.info("Created session {}", session.getId());
        if (!StringUtils.hasText(session.getName())) {
            session.setName(session.getId().toString());
            session = this.sessionRepository.save(session);
        }

        return session;
    }

    public void deleteSession(String identifier) {

    }

    public void listSessions() {

    }

    public void getSession() {

    }

    public void updateSession() {

    }

    public void changeSession() {

    }

    public void startSession() {

    }

    public void stopSession() {

    }
}
