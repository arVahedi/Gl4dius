package io.gl4dius.cli.model.dto.sessionconfig;

import io.gl4dius.cli.assets.InterceptionMode;
import io.gl4dius.cli.utility.BooleanUtil;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;

@Builder
public record PhishingSessionConfig(
        String domain,
        String template,
        boolean sslStripping,
        boolean verbose
) implements SessionConfig {

    public static @NonNull PhishingSessionConfig empty() {
        return new PhishingSessionConfig(null, null, false, false);
    }

    @Override
    public SessionConfig update(@NonNull String key, String value) {
        return switch (key.toLowerCase()) {
            case "domain" ->
                    PhishingSessionConfig.builder().template(this.template).verbose(this.verbose).sslStripping(this.sslStripping).domain(value).build();
            case "verbose" ->
                    PhishingSessionConfig.builder().template(this.template).domain(this.domain).sslStripping(this.sslStripping).verbose(BooleanUtil.parseBoolean(value)).build();
            case "sslstripping" ->
                    PhishingSessionConfig.builder().template(this.template).domain(this.domain).verbose(this.verbose).sslStripping(BooleanUtil.parseBoolean(value)).build();
            case "template" -> {
                if (!Files.exists(Path.of(value))) {
                    throw new IllegalArgumentException("Template file does not exist: %s".formatted(value));
                }
                yield PhishingSessionConfig.builder().template(value).domain(this.domain).sslStripping(this.sslStripping).verbose(this.verbose).build();
            }
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
        return InterceptionMode.PHISHING;
    }
}
