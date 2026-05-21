package io.gl4dius.cli.render;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonOutputRenderer implements CommandOutputRenderer {

    private final ObjectMapper objectMapper;

    @Override
    public String render(Object commandResult) {
        try {
            return switch (commandResult) {
                case null -> "";
                case String stringOutput -> stringOutput;
                case Integer integerOutput -> String.valueOf(integerOutput);
                default -> this.objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(commandResult);
            };
        } catch (Exception e) {
            throw new IllegalStateException("Could not render output as JSON", e);
        }
    }
}
