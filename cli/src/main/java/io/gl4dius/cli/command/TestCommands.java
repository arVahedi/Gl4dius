package io.gl4dius.cli.command;

import io.gl4dius.cli.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommands {

    private final PreferencesRepository preferencesRepository;

    @Command(value = "test")
    public void test() throws Exception {
        log.info("Running test command");

    }
}
