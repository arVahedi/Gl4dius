package io.gl4dius.cli.model.dto.sessionconfig;

import io.gl4dius.cli.assets.InterceptionMode;
import io.gl4dius.cli.utility.BooleanUtil;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;

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
            case "template" -> {
                if (!Files.exists(Path.of(value))) {
                    throw new IllegalArgumentException("Template file does not exist: %s".formatted(value));
                }
                yield DefacingSessionConfig.builder().template(value).verbose(this.verbose).build();
            }
            case "verbose" ->
                    DefacingSessionConfig.builder().template(this.template).verbose(BooleanUtil.parseBoolean(value)).build();
            default ->
                    throw new IllegalArgumentException("Unknown config key: %s for mode: %s".formatted(key, this.mode()));
        };
    }

    @Override
    public void validate() {
        if (this.template == null) {
            throw new IllegalArgumentException("Template is required for defacing mode");
        }

        if (!Files.exists(Path.of(this.template))) {
            throw new IllegalArgumentException("Template file does not exist: %s".formatted(this.template));
        }
    }

    @Override
    public InterceptionMode mode() {
        return InterceptionMode.DEFACING;
    }
}
