package io.gl4dius.cli.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Administrative", description = "Administrative commands")
public class AdministrativeCommands {

    //todo: set global configuration

    //todo: clean-up
}
