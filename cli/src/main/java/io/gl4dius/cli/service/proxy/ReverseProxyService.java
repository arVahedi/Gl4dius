package io.gl4dius.cli.service.proxy;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.netty.handler.codec.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReverseProxyService {

    private final ProxyRequestExchanger proxyRequestExchanger;

    public Mono<ProxyResponse> forwardRequest(ProxyRequest request, boolean enableSslStripping) {
        return Mono.defer(() -> {
                    String targetUrl = resolveTargetUrl(request);

                    return this.proxyRequestExchanger.exchange(request.method(), targetUrl, request.headers(), request.body())
                            .flatMap(response -> {
                                if (enableSslStripping && isHttpsRedirection(targetUrl, response)) {
                                    var redirectMethod = resolveRedirectMethod(request.method(), response.status());
                                    byte[] redirectBody = shouldPreserveRedirectBody(response.status())
                                            ? request.body()
                                            : new byte[0];

                                    return this.proxyRequestExchanger.exchange(redirectMethod, response.headers().get(HttpHeaderNames.LOCATION), request.headers(), redirectBody);
                                }

                                return Mono.just(response);
                            });
                })
                .onErrorResume(ex -> Mono.just(new ProxyResponse(
                        HttpResponseStatus.BAD_GATEWAY,
                        new DefaultHttpHeaders().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; charset=UTF-8"),
                        ("Proxy error: " + ex.getMessage()).getBytes(StandardCharsets.UTF_8)
                )));
    }

    private boolean isHttpsRedirection(@NonNull String originalTargetUrl, @NonNull ProxyResponse response) {
        if (!isRedirectStatus(response.status())) {
            return false;
        }

        String location = response.headers().get(HttpHeaderNames.LOCATION);
        if (location == null || !isAbsoluteHttpsUrl(location)) {
            return false;
        }

        try {
            return normalizeForSslRedirectComparison(originalTargetUrl)
                    .equals(normalizeForSslRedirectComparison(location));
        } catch (IllegalArgumentException ex) {
            log.debug("Skipping HTTPS redirect follow for malformed redirect location: {}", location, ex);
            return false;
        }
    }

    private boolean isRedirectStatus(@NonNull HttpResponseStatus status) {
        return status.equals(HttpResponseStatus.MOVED_PERMANENTLY)
                || status.equals(HttpResponseStatus.FOUND)
                || status.equals(HttpResponseStatus.SEE_OTHER)
                || status.equals(HttpResponseStatus.TEMPORARY_REDIRECT)
                || status.equals(HttpResponseStatus.PERMANENT_REDIRECT);
    }

    private boolean isAbsoluteHttpsUrl(@NonNull String url) {
        try {
            URI uri = new URI(url);
            return uri.isAbsolute() && "https".equalsIgnoreCase(uri.getScheme());
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private String normalizeForSslRedirectComparison(@NonNull String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("URL has no host: " + url);
            }

            if (host.regionMatches(true, 0, "www.", 0, 4)) {
                host = host.substring(4);
            }

            return new URI(
                    "https",
                    uri.getUserInfo(),
                    host.toLowerCase(),
                    uri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            ).toString();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed URL: " + url, ex);
        }
    }

    private @NonNull HttpMethod resolveRedirectMethod(@NonNull HttpMethod originalMethod, @NonNull HttpResponseStatus status) {
        if (shouldPreserveRedirectBody(status) || originalMethod.equals(HttpMethod.HEAD)) {
            return originalMethod;
        }

        return HttpMethod.GET;
    }

    private boolean shouldPreserveRedirectBody(@NonNull HttpResponseStatus status) {
        return status.equals(HttpResponseStatus.TEMPORARY_REDIRECT)
                || status.equals(HttpResponseStatus.PERMANENT_REDIRECT);
    }

    private @NonNull String resolveTargetUrl(@NonNull ProxyRequest request) {
        String uri = request.uri();
        if (uri.startsWith("http://") || uri.startsWith("https://")) {
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
