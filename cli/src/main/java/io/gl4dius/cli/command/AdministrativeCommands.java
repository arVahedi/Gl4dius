package io.gl4dius.cli.command;

import io.gl4dius.cli.module.firewall.IptablesRuleManager;
import io.gl4dius.cli.module.ipforwarding.Ipv4ForwardingManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Administrative", description = "Administrative commands")
public class AdministrativeCommands {

    private final IptablesRuleManager iptablesRuleManager;
    private final Ipv4ForwardingManager ipv4ForwardingManager;

    //todo: set global configuration

    //todo: show global configuration

    @Command(name = "cleanup", description = "Cleanup leftover system changes")
    public void cleanup() {
        this.iptablesRuleManager.flushRules();
        this.ipv4ForwardingManager.disableIpv4Forwarding();
    }
}
