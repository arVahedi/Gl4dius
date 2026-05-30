package io.gl4dius.cli.service;

import io.gl4dius.cli.configuration.PromptConfiguration;
import io.gl4dius.cli.model.dto.proxy.ProxyRequest;
import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataDumpService {

    public void dump(@NonNull ProxyRequest request) {
        dump(request, PromptConfiguration.DEFAULT_PROMPT_COLOR);
    }

    public void dump(@NonNull ProxyResponse response) {
        dump(response, PromptConfiguration.DEFAULT_PROMPT_COLOR);
    }

    public void dump(@NonNull ProxyRequest request, int color) {
        String message = "----- HTTP REQUEST DUMP -----" + "\n" +
                "method=" + request.method() + "\n" +
                "uri=" + request.uri() + "\n" +
                "host=" + request.host() + "\n" +
                "headers:" + dumpHeaders(request.headers()) + "\n" +
                "body=" + new String(request.body(), StandardCharsets.UTF_8) + "\n" +
                "-----------------------------";

        log.info(styleMessage(message, color));
    }

    public void dump(@NonNull ProxyResponse response, int color) {
        String sb = "----- HTTP RESPONSE DUMP -----" + "\n" +
                "status:" + response.status() + "\n" +
                "headers:" + dumpHeaders(response.headers()) + "\n" +
                "body=" + new String(response.body(), StandardCharsets.UTF_8) + "\n" +
                "-----------------------------";

        log.info(styleMessage(sb, color));
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

    private String styleMessage(String message, int color) {
        return new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(color))
                .append(message)
                .style(AttributedStyle.DEFAULT.foreground(PromptConfiguration.DEFAULT_PROMPT_COLOR))
                .toAnsi();
    }
}
