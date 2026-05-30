package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Interceptor {

    default @NonNull Mono<ProxyResponse> fromFileContent(Path path) {
        return Mono.fromCallable(() -> {
            Path normalized = path.toAbsolutePath().normalize();

            if (!Files.exists(normalized) || Files.isDirectory(normalized)) {
                return ProxyResponse.of("<html><body><h1>Static page not found</h1></body></html>", HttpHeaderValues.TEXT_HTML + "; charset=UTF-8");
            }

            String html = Files.readString(normalized, StandardCharsets.UTF_8);
            return ProxyResponse.of(html, HttpHeaderValues.TEXT_HTML + "; charset=UTF-8");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    Mono<ProxyResponse> intercept(ProxyRequest request);
}
