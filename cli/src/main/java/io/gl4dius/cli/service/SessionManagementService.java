package io.gl4dius.cli.service;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.utility.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManagementService {

    private final SessionRepository sessionRepository;
    private final SessionExecutionService sessionExecutionService;

    @Transactional
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

        log.info("Switching session to {}", session.getName());
        this.sessionExecutionService.switchSession(session.getName());

        return session;
    }

    @Transactional
    public Session deleteSession(String identifier) {
        var session = this.getSession(identifier);

        this.sessionExecutionService.stopSession();
        Gl4diusApplication.getCurrentSession().ifPresent(currentSession -> {
            if (currentSession.getId().equals(session.getId())) {
                this.sessionExecutionService.exitSession();
            }
        });

        this.sessionRepository.delete(session);
        log.info("Deleted session {}", session.getId());
        return session;
    }

    public List<Session> listSessions() {
        return this.sessionRepository.findAll();
    }

    public Session getSession(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            identifier = Gl4diusApplication.getCurrentSession()
                    .orElseThrow(() -> new IllegalArgumentException("Session identifier must not be blank when current session is not set"))
                    .getName();
        }

        final String finalIdentifier = identifier;
        return UuidUtil.parseUuidIfValid(identifier)
                .flatMap(this.sessionRepository::findById)
                .or(() -> this.sessionRepository.findByName(finalIdentifier))
                .orElseThrow(() -> new IllegalArgumentException("Session %s not found".formatted(finalIdentifier)));
    }

    @Transactional
    public Session updateSession(String identifier, String name, String description) {
        var session = this.getSession(identifier);

        if (StringUtils.hasText(name)) {
            session.setName(name);
        }
        if (StringUtils.hasText(description)) {
            session.setDescription(description);
        }

        session = this.sessionRepository.save(session);
        log.info("Updated session {}", session.getId());
        return session;
    }
}
