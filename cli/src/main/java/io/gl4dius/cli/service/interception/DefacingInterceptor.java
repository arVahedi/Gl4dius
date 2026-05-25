package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.model.dto.sessionconfig.DefacingSessionConfig;
import io.gl4dius.cli.module.arp.ArpPoisoner;
import io.gl4dius.cli.service.DaemonModuleExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefacingInterceptor implements Interceptor {

    private final DaemonModuleExecutor daemonModuleExecutor;
    private final ArpPoisoner arpPoisoner;

    static @NonNull Mono<ProxyResponse> readHtml(Path path) {
        return Mono.fromCallable(() -> {
            Path normalized = path.toAbsolutePath().normalize();

            if (!Files.exists(normalized) || Files.isDirectory(normalized)) {
                return ProxyResponse.html("<html><body><h1>Static page not found</h1></body></html>");
            }

            String html = Files.readString(normalized, StandardCharsets.UTF_8);
            return ProxyResponse.html(html);
        });

    }

    public Mono<ProxyResponse> intercept(ProxyRequest request) {
        var config = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Session has not been initialized"))
                .getConfig();
        var path = Path.of(((DefacingSessionConfig) config).template());
        return readHtml(path);
    }
}
