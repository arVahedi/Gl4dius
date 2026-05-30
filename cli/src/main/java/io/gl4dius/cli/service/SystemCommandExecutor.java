package io.gl4dius.cli.service;

import io.gl4dius.cli.model.dto.system.CommandRequest;
import io.gl4dius.cli.model.dto.system.CommandResult;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemCommandExecutor implements AutoCloseable {

    private final ExecutorService streamReaderExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public CommandResult execute(@NonNull CommandRequest request) {
        Process process = null;
        try {
            process = new ProcessBuilder(request.command())
                    .redirectErrorStream(false)
                    .start();

            Future<String> stdoutFuture = readAsync(process.getInputStream());
            Future<String> stderrFuture = readAsync(process.getErrorStream());

            boolean finished = process.waitFor(request.timeout().toMillis(), TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroyForcibly();
                log.debug("Command timed out: {}", String.join(" ", request.command()));
                return new CommandResult(request.command(), CommandResult.EXIT_TIMEOUT, "", "");
            }

            return new CommandResult(
                    request.command(),
                    process.exitValue(),
                    stdoutFuture.get(1, TimeUnit.SECONDS),
                    stderrFuture.get(1, TimeUnit.SECONDS)
            );

        } catch (IOException ex) {
            throw new IllegalStateException("Could not start command: " + String.join(" ", request.command()), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();

            throw new IllegalStateException("Command interrupted: " + String.join(" ", request.command()), ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Could not read command output: " + String.join(" ", request.command()), ex);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        this.streamReaderExecutor.close();
    }

    private @NonNull Future<String> readAsync(InputStream inputStream) {
        return streamReaderExecutor.submit(() -> {
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            return output.toString();
        });
    }
}

