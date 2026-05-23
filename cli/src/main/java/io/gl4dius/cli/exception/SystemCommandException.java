package io.gl4dius.cli.exception;

import io.gl4dius.cli.model.dto.system.CommandResult;
import org.jspecify.annotations.NonNull;

public class SystemCommandException extends RuntimeException {

    public SystemCommandException(@NonNull CommandResult result) {
        super("""
                Command failed.
                Command: %s
                Exit code: %d
                Stderr: %s
                Stdout: %s
                """.formatted(
                String.join(" ", result.command()),
                result.exitCode(),
                result.stderr(),
                result.stdout()
        ));

    }

}