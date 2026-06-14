package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.service.SystemCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IptablesOutputManager extends IptablesRuleManager {

    @Autowired
    public IptablesOutputManager(SystemCommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public void blockIcmp() {
        executeOrThrow(List.of("iptables", "-A", getChain(), "-p", "icmp", "--icmp-type", "time-exceeded", "-j", "DROP"));
        executeOrThrow(List.of("iptables", "-A", getChain(), "-p", "icmp", "--icmp-type", "destination-unreachable", "-j", "DROP"));
    }

    @Override
    public String getBuiltinChain() {
        return "OUTPUT";
    }
}
