package io.gl4dius.cli.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferencesKey {
    PROXY_SERVER_PORT("PPORT"),
    WEB_SERVER_PORT("WPORT"),
    ;

    private final String acronym;
}
