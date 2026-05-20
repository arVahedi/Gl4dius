package io.gl4dius.cli.configuration;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.model.entity.Session;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@RequiredArgsConstructor
public class PromptConfiguration {

    private final BusinessConfiguration businessConfiguration;

    @Bean
    public PromptProvider promptProvider() {
        return () -> {
            String sessionName = Gl4diusApplication.getCurrentSession()
                    .map(Session::getName)
                    .map(this::sanitizePromptPart)
                    .map(name -> "[" + name + "]")
                    .orElse("");

            return new AttributedString(
                    this.businessConfiguration.getPromptKey() + sessionName + ":> ",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
            );
        };
    }

    private @NonNull String sanitizePromptPart(@NonNull String value) {
        return value
                .replace("\n", "")
                .replace("\r", "")
                .trim();
    }
}
