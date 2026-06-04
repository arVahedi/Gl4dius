package io.gl4dius.cli.model.dto.proxy;

import io.gl4dius.cli.assets.InterceptionMode;
import lombok.Builder;

@Builder
public record ProxyServerRuntimeConfig(
        String host,
        int port,
        String staticResourceIdentifier,
        InterceptionMode interceptionMode,
        String templatePath
) {
}
