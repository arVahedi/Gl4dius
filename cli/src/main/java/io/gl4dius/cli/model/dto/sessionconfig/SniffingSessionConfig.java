package io.gl4dius.cli.model.dto.sessionconfig;

import io.gl4dius.cli.assets.InterceptionMode;
import io.gl4dius.cli.utility.BooleanUtil;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record SniffingSessionConfig(
        boolean sslStripping
) implements SessionConfig {

    public static @NonNull SniffingSessionConfig empty() {
        return new SniffingSessionConfig(false);
    }

    @Override
    public SessionConfig update(@NonNull String key, String value) {
        return switch (key.toLowerCase()) {
            case "sslstripping" ->
                    SniffingSessionConfig.builder().sslStripping(BooleanUtil.parseBoolean(value)).build();
            default ->
                    throw new IllegalArgumentException("Unknown config key: %s for mode: %s".formatted(key, this.mode()));
        };
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
