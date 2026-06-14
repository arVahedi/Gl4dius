package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.service.SystemCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IptablesForwardManager extends IptablesRuleManager {

    @Autowired
    public IptablesForwardManager(SystemCommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public void addForwardRule(@NonNull String input, @NonNull String output) {
        executeOrThrow(List.of("iptables", "-A", getChain(), "-i", input, "-o", output, "-j", "ACCEPT"));
    }

    @Override
    public String getBuiltinChain() {
        return "FORWARD";
    }
}
