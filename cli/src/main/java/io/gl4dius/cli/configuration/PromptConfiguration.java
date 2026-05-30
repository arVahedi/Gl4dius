package io.gl4dius.cli.configuration;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.entity.Session;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@RequiredArgsConstructor
public class PromptConfiguration {

    public static final int DEFAULT_PROMPT_COLOR = AttributedStyle.GREEN;

    private final BusinessConfiguration businessConfiguration;

    @Bean
    public PromptProvider promptProvider() {
        return () -> {
            AttributedStringBuilder builder = new AttributedStringBuilder();

            builder.style(AttributedStyle.DEFAULT.foreground(DEFAULT_PROMPT_COLOR))
                    .append(this.businessConfiguration.getPromptKey());

            var sessionColor = Gl4diusApplication.isCurrentSessionRunning() ? AttributedStyle.RED : AttributedStyle.MAGENTA;

            Gl4diusApplication.getCurrentSession()
                    .map(Session::getName)
                    .map(this::sanitizePromptPart)
                    .ifPresent(name -> builder
                            .append("[")
                            .style(AttributedStyle.DEFAULT.foreground(sessionColor))
                            .append(name)
                            .style(AttributedStyle.DEFAULT.foreground(DEFAULT_PROMPT_COLOR))
                            .append("]"));

            builder.style(AttributedStyle.DEFAULT.foreground(DEFAULT_PROMPT_COLOR))
                    .append(":> ");

            return builder.toAttributedString();
        };
    }

    private @NonNull String sanitizePromptPart(@NonNull String value) {
        return value
                .replace("\n", "")
                .replace("\r", "")
                .trim();
    }
}
