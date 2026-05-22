package io.gl4dius.cli.model;

import io.gl4dius.cli.assets.AttackMode;
import io.gl4dius.cli.utility.BooleanUtil;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record DefacingSessionConfig(
        String template,
        boolean verbose
) implements SessionConfig {

    public static @NonNull DefacingSessionConfig empty() {
        return new DefacingSessionConfig(null, false);
    }

    public @NonNull DefacingSessionConfig update(@NonNull String key, String value) {
        return switch (key.toLowerCase()) {
            case "template" -> DefacingSessionConfig.builder().template(value).verbose(this.verbose).build();
            case "verbose" ->
                    DefacingSessionConfig.builder().template(this.template).verbose(BooleanUtil.parseBoolean(value)).build();
            default ->
                    throw new IllegalArgumentException("Unknown config key: %s for mode: %s".formatted(key, this.mode()));
        };
    }

    @Override
    public AttackMode mode() {
        return AttackMode.DEFACING;
    }
}
