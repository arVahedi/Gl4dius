package io.gl4dius.cli.command;

import io.gl4dius.cli.assets.InterceptionMode;
import io.gl4dius.cli.service.session.SessionConfigService;
import io.gl4dius.cli.service.session.SessionManagementService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Session Configuration", description = "session configuration commands")
public class SessionConfigCommands {

    private final SessionManagementService sessionManagementService;
    private final SessionConfigService sessionConfigService;

    @Command(name = "session mode", description = "Set interception mode on current session")
    public void configureMode(
            @Argument(index = 0, description = "Interception mode")
            String mode
    ) {
        var interceptionMode = InterceptionMode.fromString(mode);
        this.sessionConfigService.configureSessionMode(interceptionMode);
    }

    @Command(name = "session config set", description = "Set config on current session")
    public void configureSession(
            @Argument(index = 0, description = "Key configuration")
            @NotBlank
            String key,
            @Argument(index = 1, description = "Value to set")
            @NotBlank
            String value
    ) {
        this.sessionConfigService.updateSessionConfig(key, value);
    }

    @Command(name = "session config show", description = "Show current session configuration")
    public Object showConfig() {
        var session = this.sessionManagementService.getSession(null);
        return session.getConfig() != null
                ? session.getConfig()
                : "Set session mode first by running 'session mode %s'"
                .formatted(Arrays.stream(InterceptionMode.values()).map(InterceptionMode::getShortName).toList().toString());
    }
}
