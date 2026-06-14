package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.service.SystemCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IptablesPostRoutingManager extends IptablesRuleManager {

    @Autowired
    public IptablesPostRoutingManager(SystemCommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public void enableMasquerade(@NonNull String nic) {
        executeOrThrow(List.of("iptables", "-t", getTable(), "-A", getBuiltinChain(), "-o", nic, "-j", "MASQUERADE"));
    }

    @Override
    public String getTable() {
        return "nat";
    }

    @Override
    public String getBuiltinChain() {
        return "POSTROUTING";
    }
}
