package io.gl4dius.cli.service.proxy;

import io.gl4dius.cli.assets.InterceptionMode;
import io.gl4dius.cli.service.interception.DefacingInterceptor;
import io.gl4dius.cli.service.interception.Interceptor;
import io.gl4dius.cli.service.interception.PhishingInterceptor;
import io.gl4dius.cli.service.interception.SniffingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyInterceptorRouter {

    private final DefacingInterceptor defacingInterceptor;
    private final SniffingInterceptor sniffingInterceptor;
    private final PhishingInterceptor phishingInterceptor;

    public Interceptor route(InterceptionMode mode) {
        return switch (mode) {
            case DEFACING -> this.defacingInterceptor;
            case SNIFFING -> this.sniffingInterceptor;
            case PHISHING -> this.phishingInterceptor;
        };
    }
}
