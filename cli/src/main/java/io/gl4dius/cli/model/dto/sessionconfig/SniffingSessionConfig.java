package io.gl4dius.cli.model.dto.sessionconfig;

import io.gl4dius.cli.assets.InterceptionMode;
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
    public void validate() {
        // There is nothing to validate
    }

    @Override
    public InterceptionMode mode() {
        return InterceptionMode.SNIFFING;
    }
}
