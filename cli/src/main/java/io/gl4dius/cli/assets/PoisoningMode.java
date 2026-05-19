package io.gl4dius.cli.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PoisoningMode {
    TARGET_SPECIFIC("ts"),
    BROADCAST("bc");

    private final String acronym;

    public static @NonNull PoisoningMode fromAcronym(@NonNull String acronym) {
        return Arrays.stream(PoisoningMode.values())
                .filter(mode -> mode.getAcronym().equalsIgnoreCase(acronym))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown poisoning mode: %s".formatted(acronym)));
    }
}
