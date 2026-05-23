package io.gl4dius.cli;

import io.gl4dius.cli.model.dto.system.CommandRequest;
import io.gl4dius.cli.service.SystemCommandExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinuxPrivilegeChecker {

    private final SystemCommandExecutor commandExecutor;

    @PostConstruct
    public void check() {
        if (!this.isRoot()) {
            log.error("You are not authorized to run this application! root privilege is required.");
            System.exit(1);
        }
    }

    public boolean isRoot() {
        String uid = this.commandExecutor.execute(CommandRequest.of(Duration.ofSeconds(2), "id", "-u"))
                .requireStdout().trim();
        return "0".equals(uid);
    }
}
