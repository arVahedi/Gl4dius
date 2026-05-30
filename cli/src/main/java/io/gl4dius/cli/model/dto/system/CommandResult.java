package io.gl4dius.cli.model.dto.system;

import io.gl4dius.cli.exception.SystemCommandException;

import java.util.List;

public record CommandResult(
        List<String> command,
        int exitCode,
        String stdout,
        String stderr
) {

    public static final int EXIT_TIMEOUT = -1;
    public static final int EXIT_INTERRUPTED = -2;

    public boolean succeeded() {
        return exitCode == 0;
    }

    public String requireStdout() {
        if (!succeeded()) {
            throw new SystemCommandException(this);
        }

        return stdout;
    }
}
