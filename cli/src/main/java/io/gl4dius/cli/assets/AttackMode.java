package io.gl4dius.cli.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AttackMode {
    DEFACING("def"),
    SNIFFING("sn"),
    PHISHING("ph");

    private final String shortName;

    public static @NonNull AttackMode fromString(String value) {
        return Arrays.stream(AttackMode.values())
                .filter(mode -> mode.getShortName().equalsIgnoreCase(value) || mode.name().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown attack mode: %s".formatted(value)));
    }
}
