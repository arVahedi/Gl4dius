package io.gl4dius.cli.model;

import io.gl4dius.cli.assets.AttackMode;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record PhishingSessionConfig(
        String domain,
        String template
) implements SessionConfig {

    public static @NonNull PhishingSessionConfig empty() {
        return new PhishingSessionConfig(null, null);
    }

    @Override
    public SessionConfig update(String key, String value) {
        return switch (key.toLowerCase()) {
            case "domain" -> PhishingSessionConfig.builder().template(this.template).domain(value).build();
            case "template" -> PhishingSessionConfig.builder().template(value).domain(this.domain).build();
            default ->
                    throw new IllegalArgumentException("Unknown config key: %s for mode: %s".formatted(key, this.mode()));
        };
    }

    @Override
    public AttackMode mode() {
        return AttackMode.PHISHING;
    }
}
