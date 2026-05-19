package io.gl4dius.cli.model;

import io.gl4dius.cli.assets.AttackMode;

public record PhishingSessionConfig(

) implements SessionConfig {

    @Override
    public AttackMode mode() {
        return AttackMode.PHISHING;
    }
}
