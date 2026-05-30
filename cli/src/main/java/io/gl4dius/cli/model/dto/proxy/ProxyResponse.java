package io.gl4dius.cli.model.dto.proxy;

import io.netty.handler.codec.http.*;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record ProxyResponse(
        HttpResponseStatus status,
        HttpHeaders headers,
        byte[] body
) {
    public static @NonNull ProxyResponse html(@NonNull String html) {
        return new ProxyResponse(
                HttpResponseStatus.OK,
                new DefaultHttpHeaders().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML + "; charset=UTF-8"),
                html.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
