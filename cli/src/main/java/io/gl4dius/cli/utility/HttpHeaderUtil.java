package io.gl4dius.cli.utility;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Set;

@UtilityClass
public class HttpHeaderUtil {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade"
    );

    public boolean isHopByHopHeader(@NonNull String name) {
        return HOP_BY_HOP_HEADERS.contains(name.toLowerCase(Locale.ROOT));
    }
}
