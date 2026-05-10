package io.gl4dius.cli.configuration;

import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@RequiredArgsConstructor
public class PromptConfiguration implements PromptProvider {

    private final BusinessConfiguration businessConfiguration;

    @Override
    public @NonNull AttributedString getPrompt() {
        return new AttributedString(this.businessConfiguration.getPromptKey(),
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        );
    }
}
