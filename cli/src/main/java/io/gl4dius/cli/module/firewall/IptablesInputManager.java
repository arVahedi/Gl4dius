package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.service.SystemCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IptablesInputManager extends IptablesRuleManager {

    @Autowired
    public IptablesInputManager(SystemCommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public void blockUdpTraceroute() {
        executeOrThrow(List.of("iptables", "-A", getChain(), "-p", "udp", "--dport", "33434:33534", "-j", "DROP"));
    }

    @Override
    public String getBuiltinChain() {
        return "INPUT";
    }
}
