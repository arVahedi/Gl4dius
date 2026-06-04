package io.gl4dius.cli.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PreferencesKey {
    PROXY_SERVER_PORT("PPORT"),
    WEB_SERVER_PORT("WPORT"),
    STATIC_RESOURCE_URI("SRU")
    ;

    private final String acronym;

    public static @NonNull PreferencesKey fromAcronym(@NonNull String acronym) {
        return Arrays.stream(PreferencesKey.values())
                .filter(item -> item.acronym.equalsIgnoreCase(acronym))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown preferences key: %s".formatted(acronym)));
    }
}
