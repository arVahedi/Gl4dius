package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SniffingInterceptor implements Interceptor {

    @Override
    public Mono<ProxyResponse> intercept(ProxyRequest request) {
        return null;
    }
}
