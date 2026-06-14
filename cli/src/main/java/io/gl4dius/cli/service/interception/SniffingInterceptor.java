package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.model.dto.sessionconfig.SniffingSessionConfig;
import io.gl4dius.cli.service.DataDumpService;
import io.gl4dius.cli.service.proxy.ReverseProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class SniffingInterceptor implements Interceptor {

    private final DataDumpService dataDumpService;
    private final ReverseProxyService reverseProxyService;

    @Override
    public Mono<ProxyResponse> intercept(ProxyRequest request) {
        this.dataDumpService.dump(request);

        var config = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"))
                .getConfig();

        if (config instanceof SniffingSessionConfig(boolean sslStripping)) {
            return this.reverseProxyService.forwardRequest(request, sslStripping)
                    .doOnNext(this.dataDumpService::dump);
        }

        return Mono.empty();
    }
}
