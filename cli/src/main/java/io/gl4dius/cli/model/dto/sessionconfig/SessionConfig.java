package io.gl4dius.cli.model.dto.sessionconfig;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.gl4dius.cli.assets.InterceptionMode;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "mode"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefacingSessionConfig.class, name = "DEFACING"),
        @JsonSubTypes.Type(value = SniffingSessionConfig.class, name = "SNIFFING"),
        @JsonSubTypes.Type(value = PhishingSessionConfig.class, name = "PHISHING")
})
public sealed interface SessionConfig
        permits DefacingSessionConfig, SniffingSessionConfig, PhishingSessionConfig {

    SessionConfig update(String key, String value);

    InterceptionMode mode();
}
