package io.gl4dius.cli.configuration;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class PromptConfiguration implements PromptProvider {

    private final BusinessConfiguration businessConfiguration;

    public PromptConfiguration(BusinessConfiguration businessConfiguration) {
        this.businessConfiguration = businessConfiguration;
    }

    @Override
    public @NonNull AttributedString getPrompt() {
        return new AttributedString(this.businessConfiguration.getPromptKey(),
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        );
    }
}
