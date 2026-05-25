package io.gl4dius.cli.model.dto.proxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Builder
public record ProxyResponse(
        HttpResponseStatus status,
        Map<String, String> headers,
        byte[] body
) {
    public static @NonNull ProxyResponse html(@NonNull String html) {
        return new ProxyResponse(
                HttpResponseStatus.OK,
                Map.of("Content-Type", "text/html; charset=UTF-8"),
                html.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
