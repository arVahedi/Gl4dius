package io.gl4dius.cli.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum InterceptionMode {
    DEFACING("def"),
    SNIFFING("sn"),
    PHISHING("ph");

    private final String shortName;

    public static @NonNull InterceptionMode fromString(String value) {
        return Arrays.stream(InterceptionMode.values())
                .filter(mode -> mode.getShortName().equalsIgnoreCase(value) || mode.name().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown interception mode: %s".formatted(value)));
    }
}
