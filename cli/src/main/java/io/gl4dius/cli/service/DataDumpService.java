package io.gl4dius.cli.service;

import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataDumpService {

    public void dump(@NonNull ProxyRequest request) {
        String sb = "----- HTTP REQUEST DUMP -----" + "\n" +
                "method=" + request.method() + "\n" +
                "uri=" + request.uri() + "\n" +
                "host=" + request.host() + "\n" +
                "headers:" + dumpHeaders(request.headers()) + "\n" +
                "body=" + new String(request.body(), StandardCharsets.UTF_8) + "\n" +
                "-----------------------------";

        log.info(sb);
    }

    public void dump(@NonNull ProxyResponse response) {
        String sb = "----- HTTP RESPONSE DUMP -----" + "\n" +
                "status:" + response.status() + "\n" +
                "headers:" + dumpHeaders(response.headers()) + "\n" +
                "body=" + new String(response.body(), StandardCharsets.UTF_8) + "\n" +
                "-----------------------------";

        log.info(sb);
    }

    private @NonNull String dumpHeaders(@NonNull HttpHeaders headers) {
        var sb = new StringBuilder();
        headers.forEach(entry -> {
            String name = entry.getKey();
            String value = entry.getValue();
            sb.append(name).append(": ").append(value).append("\n");
        });
        return sb.toString();
    }
}
