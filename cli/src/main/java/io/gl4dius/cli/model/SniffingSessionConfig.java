package io.gl4dius.cli.model;

import io.gl4dius.cli.assets.AttackMode;
import org.jspecify.annotations.NonNull;

public record SniffingSessionConfig(

) implements SessionConfig {

    public static @NonNull SniffingSessionConfig empty() {
        return new SniffingSessionConfig();
    }

    @Override
    public SessionConfig update(String key, String value) {
        return null;
    }

    @Override
    public AttackMode mode() {
        return AttackMode.SNIFFING;
    }
}
