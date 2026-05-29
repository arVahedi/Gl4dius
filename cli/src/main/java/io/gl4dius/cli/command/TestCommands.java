package io.gl4dius.cli.command;

import io.gl4dius.cli.repository.PreferencesRepository;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommands {

    private final PreferencesRepository preferencesRepository;

    @Command(value = "test")
    public String test() throws Exception {
        log.info("Running test command");
        var ipv4Subnet = NetInterfaceUtil.resolveIpv4Subnet("eth0");

        ipv4Subnet.hosts().forEach(System.out::println);

        return StreamSupport.stream(ipv4Subnet.hosts().spliterator(), false).toList().toString();
    }
}
