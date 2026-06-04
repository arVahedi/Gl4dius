package io.gl4dius.cli.service.proxy;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.model.dto.proxy.ProxyServerRuntimeConfig;
import io.gl4dius.cli.service.webserver.WebResourceService;
import io.micrometer.common.util.StringUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerResponse;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyServerEngine {

    private final ProxyInterceptorRouter router;
    private final WebResourceService webResourceService;
    private final AtomicReference<DisposableServer> serverRef = new AtomicReference<>();

    public synchronized void start(@NonNull ProxyServerRuntimeConfig config) {
        log.debug("Starting proxy server on {}:{}", config.host(), config.port());

        var disposableServer = HttpServer.create()
                .host(config.host())
                .port(config.port())
                .handle((request, response) ->
                        request.receive()
                                .aggregate()
                                .asByteArray()
                                .defaultIfEmpty(new byte[0])
                                .flatMap(body -> {

                                    if ((request.uri().toLowerCase().startsWith("/" + config.staticResourceIdentifier().toLowerCase())
                                            || request.uri().toLowerCase().startsWith(config.staticResourceIdentifier().toLowerCase()))
                                            && StringUtils.isNotBlank(config.templatePath())) {
                                        return this.webResourceService.serve(request, response, config.templatePath(), config.staticResourceIdentifier());
                                    }

                                    var proxyRequest = ProxyRequest.builder()
                                            .method(request.method())
                                            .uri(request.uri())
                                            .path(request.path())
                                            .host(request.requestHeaders().get(HttpHeaderNames.HOST))
                                            .headers(request.requestHeaders())
                                            .body(body)
                                            .build();

                                    var interceptor = this.router.route(config.interceptionMode());
                                    return interceptor.intercept(proxyRequest)
                                            .flatMap(proxyResponse -> writeResponse(response, proxyResponse));
                                })
                )
                .bindNow();

        this.serverRef.set(disposableServer);
    }

    public synchronized void stop() {
        DisposableServer server = this.serverRef.getAndSet(null);
        if (server != null) {
            server.disposeNow();
        }
    }

    public boolean isRunning() {
        return this.serverRef.get() != null;
    }

    private @NonNull Mono<Void> writeResponse(@NonNull HttpServerResponse response, @NonNull ProxyResponse proxyResponse) {
        response.status(proxyResponse.status());
        proxyResponse.headers().forEach(header -> response.header(header.getKey(), header.getValue()));
        return response.sendByteArray(Mono.just(proxyResponse.body())).then();
    }
}
