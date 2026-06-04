package io.gl4dius.cli.model.dto.proxy;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record ProxyResponse(
        HttpResponseStatus status,
        HttpHeaders headers,
        byte[] body
) {
    public static @NonNull ProxyResponse of(@NonNull String html, String contentType) {
        return new ProxyResponse(
                HttpResponseStatus.OK,
                new DefaultHttpHeaders()
                        .add(HttpHeaderNames.CONTENT_TYPE, contentType)
                        .add("X-Content-Type-Options", "nosniff")
                        .add("Cache-Control", "no-store")
                        .add("Pragma", "no-cache")
                        .add("Access-Control-Allow-Credentials", "true")
                        .add("Access-Control-Allow-Origin", "*")
                        .add("Vary", "Origin")
                        .add("Access-Control-Allow-Private-Network", "true"),
                html.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
