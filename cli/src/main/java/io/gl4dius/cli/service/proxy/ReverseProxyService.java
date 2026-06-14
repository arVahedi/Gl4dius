package io.gl4dius.cli.service.proxy;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.utility.HttpHeaderUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReverseProxyService {

    private final HttpClient httpClient = HttpClient.create().followRedirect(false);

    public Mono<ProxyResponse> forwardRequest(ProxyRequest request, boolean enableSslStriping) {
        String targetUrl = resolveTargetUrl(request);

        return this.httpClient
                .headers(outgoingHeaders -> {
                    request.headers().forEach(entry -> {
                        String name = entry.getKey();
                        String value = entry.getValue();
                        if (!HttpHeaderUtil.isHopByHopHeader(name)) {
                            outgoingHeaders.set(name, value);
                        }
                    });
                    outgoingHeaders.remove(HttpHeaderNames.HOST);
                })
                .request(request.method())
                .uri(targetUrl)
                .send(Mono.just(Unpooled.wrappedBuffer(request.body())))
                .responseSingle((originResponse, body) ->
                        body.asByteArray()
                                .defaultIfEmpty(new byte[0])
                                .map(responseBody -> {
                                    var headers = new DefaultHttpHeaders();
                                    originResponse.responseHeaders().forEach(entry -> {
                                        if (!HttpHeaderUtil.isHopByHopHeader(entry.getKey())
                                                && !HttpHeaderUtil.isHSTS(entry.getKey())) {
                                            headers.add(entry.getKey(), entry.getValue());
                                        }
                                    });

                                    return new ProxyResponse(
                                            originResponse.status(),
                                            headers,
                                            responseBody
                                    );
                                })
                )
                .onErrorResume(ex -> Mono.just(new ProxyResponse(
                        HttpResponseStatus.BAD_GATEWAY,
                        new DefaultHttpHeaders().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; charset=UTF-8"),
                        ("Proxy error: " + ex.getMessage()).getBytes(StandardCharsets.UTF_8)
                )));
    }

    private @NonNull String resolveTargetUrl(@NonNull ProxyRequest request) {
        String uri = request.uri();
        if (uri.startsWith("http://")) {
            return uri;
        }

        String host = request.host();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Missing Host header");
        }

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        return "http://" + host + uri;
    }
}
