package io.gl4dius.cli.command;

import io.gl4dius.cli.assets.PreferencesKey;
import io.gl4dius.cli.repository.PreferencesRepository;
import io.gl4dius.cli.service.NetDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommands {

    private final PreferencesRepository preferencesRepository;

    @Command(value = "test")
    public String test() throws Exception {
        log.info("Running test command");
        return this.preferencesRepository.findById(PreferencesKey.PROXY_SERVER_PORT)
              .orElseThrow(() -> new IllegalArgumentException("Proxy server port has not been set"))
              .getValue();
    }
}
