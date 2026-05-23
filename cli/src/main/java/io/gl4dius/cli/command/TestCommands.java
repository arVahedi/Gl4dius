package io.gl4dius.cli.command;

import io.gl4dius.cli.model.dto.iptables.IptablesRedirectRule;
import io.gl4dius.cli.module.firewall.IptablesRuleManager;
import io.gl4dius.cli.module.ipforwarding.Ipv4ForwardingManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommands {

    private final Ipv4ForwardingManager ipv4ForwardingManager;
    private final IptablesRuleManager iptablesRuleManager;

    @Command(value = "test")
    public void test() {
        ipv4ForwardingManager.enableIpv4Forwarding();
        iptablesRuleManager.addRedirectRule(IptablesRedirectRule.builder()
                .inputInterface("eth0")
                .originalPort(80)
                .destinationPort(4428)
                .destinationIp("172.28.0.1")
                .build());
    }
}
