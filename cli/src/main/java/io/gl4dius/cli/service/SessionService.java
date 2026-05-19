package io.gl4dius.cli.service;

import io.gl4dius.cli.assets.AttackMode;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Session createSession(String name, String description) {
        if (StringUtils.hasText(name) && this.sessionRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Session name %s already exists".formatted(name));
        }

        var session = new Session();
        session.setName(name);
        session.setDescription(description);

        session = this.sessionRepository.save(session);

        log.info("Created session {}", session.getId());
        if (!StringUtils.hasText(session.getName())) {
            session.setName(session.getId().toString());
            session = this.sessionRepository.save(session);
        }

        return session;
    }

    public Session deleteSession(String identifier) {
        var session = this.getSession(identifier);
        this.sessionRepository.delete(session);
        log.info("Deleted session {}", session.getId());
        return session;
    }

    public List<Session> listSessions() {
        return this.sessionRepository.findAll();
    }

    public Session getSession(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            throw new IllegalArgumentException("Session identifier must not be blank");
        }

        return parseUuid(identifier)
                .flatMap(this.sessionRepository::findById)
                .or(() -> this.sessionRepository.findByName(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Session %s not found".formatted(identifier)));
    }

    public void updateSession() {

    }

    public void changeSession() {

    }

    public void startSession() {

    }

    public void stopSession() {

    }

    private Optional<UUID> parseUuid(String identifier) {
        try {
            return Optional.of(UUID.fromString(identifier));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
