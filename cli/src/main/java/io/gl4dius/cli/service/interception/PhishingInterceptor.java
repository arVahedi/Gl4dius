package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.model.dto.sessionconfig.PhishingSessionConfig;
import io.gl4dius.cli.service.DataDumpService;
import io.gl4dius.cli.service.proxy.ReverseProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhishingInterceptor implements Interceptor {

    private final DataDumpService dataDumpService;
    private final ReverseProxyService reverseProxyService;

    @Override
    public Mono<ProxyResponse> intercept(@NonNull ProxyRequest request) {
        String host = request.host();

        var config = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"))
                .getConfig();

        if (config instanceof PhishingSessionConfig(String domain, String template, boolean verbose)) {
            log.debug("Intercepting request to host {} for domain {}", host, domain);
            if (host != null && domain != null && host.matches(domain)) {
                var path = Path.of(template);
                this.dataDumpService.dump(request, AttributedStyle.RED);
                return fromFileContent(path)
                        .doOnNext(response -> {
                            if (verbose) {
                                this.dataDumpService.dump(response);
                            }
                        });
            } else {
                return this.reverseProxyService.forwardRequest(request)
                        .doOnNext(response -> {
                            if (verbose) {
                                this.dataDumpService.dump(request);
                                this.dataDumpService.dump(response);
                            }
                        });
            }
        }

        return Mono.empty();
    }
}
