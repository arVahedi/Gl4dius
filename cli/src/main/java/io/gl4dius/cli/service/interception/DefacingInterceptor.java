package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.model.dto.sessionconfig.DefacingSessionConfig;
import io.gl4dius.cli.service.DataDumpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefacingInterceptor implements Interceptor {

    private final DataDumpService dataDumpService;

    public Mono<ProxyResponse> intercept(ProxyRequest request) {
        var config = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"))
                .getConfig();
        if (config instanceof DefacingSessionConfig(String template, boolean verbose)) {
            var path = Path.of(template);

            if (verbose) {
                this.dataDumpService.dump(request);
            }

            return fromFileContent(path);
        }

        return Mono.empty();
    }
}
