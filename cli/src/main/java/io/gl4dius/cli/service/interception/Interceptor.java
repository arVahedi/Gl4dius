package io.gl4dius.cli.service.interception;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import reactor.core.publisher.Mono;

public interface Interceptor {

    Mono<ProxyResponse> intercept(ProxyRequest request);
}
