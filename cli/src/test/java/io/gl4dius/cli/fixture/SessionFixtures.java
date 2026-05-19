package io.gl4dius.cli.fixture;

import io.gl4dius.cli.model.entity.Session;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public final class SessionFixtures {

    public @NonNull Session session() {
        var session = new Session();
        session.setId(UUID.randomUUID());
        session.setName("test");
        session.setDescription("test session");
        session.setCreatedAt(Instant.parse("2026-05-19T10:15:30Z"));
        session.setLastUpdateAt(Instant.parse("2026-05-19T10:20:30Z"));
        return session;
    }

    public @NonNull Session session(@NonNull Consumer<Session> customizer) {
        var session = session();
        customizer.accept(session);
        return session;
    }
}
