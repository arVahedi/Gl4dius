package io.gl4dius.cli.service;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.assets.AttackMode;
import io.gl4dius.cli.model.DefacingSessionConfig;
import io.gl4dius.cli.model.PhishingSessionConfig;
import io.gl4dius.cli.model.SniffingSessionConfig;
import io.gl4dius.cli.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionConfigService {

    private final SessionRepository sessionRepository;
    private final SessionExecutionService sessionExecutionService;

    @Transactional
    public void configureSessionMode(@NonNull AttackMode mode) {
        var session = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"));
        this.sessionExecutionService.stopSession();

        var config = switch (mode) {
            case DEFACING -> DefacingSessionConfig.empty();
            case SNIFFING -> SniffingSessionConfig.empty();
            case PHISHING -> PhishingSessionConfig.empty();
        };

        session.setConfig(config);
        this.sessionRepository.save(session);

        log.debug("Set session {} for attacking mode {}", session.getId(), mode);
    }

    @Transactional
    public void updateSessionConfig(@NonNull String key, @NonNull String value) {
        var session = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"));
        this.sessionExecutionService.stopSession();

        var config = Optional.ofNullable(session.getConfig())
                .orElseThrow(() -> new IllegalStateException("Set session mode first by running 'session config set mode %s'"
                        .formatted(Arrays.stream(AttackMode.values()).map(AttackMode::getShortName).toList().toString())));

        config = config.update(key, value);
        session.setConfig(config);
        this.sessionRepository.save(session);

        log.debug("Set session {} config {} to {}", session.getId(), key, value);
    }
}
