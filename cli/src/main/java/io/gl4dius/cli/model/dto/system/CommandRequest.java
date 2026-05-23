package io.gl4dius.cli.model.dto.system;

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.List;

public record CommandRequest(
        List<String> command,
        Duration timeout
) {

    public CommandRequest {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command must not be empty.");
        }

        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout must be positive.");
        }
    }

    public static @NonNull CommandRequest of(Duration timeout, String... command) {
        return new CommandRequest(List.of(command), timeout);
    }
}
