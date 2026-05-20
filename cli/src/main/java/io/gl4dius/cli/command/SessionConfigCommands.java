package io.gl4dius.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Session Configuration", description = "session configuration commands")
public class SessionConfigCommands {
}
