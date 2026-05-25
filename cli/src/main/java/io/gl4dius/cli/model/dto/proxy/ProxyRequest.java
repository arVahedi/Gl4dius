package io.gl4dius.cli.model.dto.proxy;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Builder;

@Builder
public record ProxyRequest(
        HttpMethod method,
        String uri,
        String path,
        String host,
        HttpHeaders headers,
        byte[] body
) {
}
