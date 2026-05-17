package io.gl4dius.cli.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BusinessConfiguration {

    @Value("${gl4dius.cli.prompt.key}")
    private String promptKey;
}
