package io.gl4dius.cli.service.webserver;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.dto.sessionconfig.DefacingSessionConfig;
import io.gl4dius.cli.model.dto.sessionconfig.PhishingSessionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebServerEngine {

    private final WebResourceService webResourceService;
    private final AtomicReference<DisposableServer> serverRef = new AtomicReference<>();

    public synchronized void start(String host, int port) {
        log.debug("Starting web server on {}:{}", host, port);

        var config = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"))
                .getConfig();

        var templatePath = switch (config.mode()) {
            case DEFACING -> ((DefacingSessionConfig) config).template();
            case PHISHING -> ((PhishingSessionConfig) config).template();
            default ->
                    throw new IllegalStateException("WebServer is not supported in the mode: %s".formatted(config.mode()));
        };

        var disposableServer = HttpServer.create()
                .host(host)
                .port(port)
                .handle((request, response) ->
                        this.webResourceService.serve(request, response, templatePath, "")
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
}
