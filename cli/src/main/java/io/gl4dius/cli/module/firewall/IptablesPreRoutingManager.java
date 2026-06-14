package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.model.dto.iptables.IptablesRedirectRule;
import io.gl4dius.cli.service.SystemCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IptablesPreRoutingManager extends IptablesRuleManager {

    @Autowired
    public IptablesPreRoutingManager(SystemCommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public void addRedirectRule(@NonNull IptablesRedirectRule rule) {
        if (redirectRuleExists(rule)) {
            return;
        }

        executeOrThrow(buildAddRedirectRuleCommand(rule));
    }

    public void removeRedirectRule(@NonNull IptablesRedirectRule rule) {
        executeOrThrow(List.of(
                "iptables",
                "-t", getTable(),
                "-D", getChain(),
                "-i", rule.inputInterface(),
                "-p", "tcp",
                "--dport", String.valueOf(rule.originalPort()),
                "-j", "DNAT",
                "--to-destination", rule.destinationIp() + ":" + rule.destinationPort()
        ));
    }

    private boolean redirectRuleExists(IptablesRedirectRule rule) {
        return execute(buildCheckRedirectRuleCommand(rule));
    }

    private @NonNull List<String> buildCheckRedirectRuleCommand(IptablesRedirectRule rule) {
        return buildRedirectRuleCommand("-C", rule);
    }

    private @NonNull List<String> buildAddRedirectRuleCommand(IptablesRedirectRule rule) {
        return buildRedirectRuleCommand("-A", rule);
    }

    private @NonNull List<String> buildRedirectRuleCommand(String operation,
                                                           @NonNull IptablesRedirectRule rule) {
        return List.of(
                "iptables",
                "-t", getTable(),
                operation,
                getChain(),
                "-i", rule.inputInterface(),
                "-p", "tcp",
                "--dport", String.valueOf(rule.originalPort()),
                "-j", "DNAT",
                "--to-destination", rule.destinationIp() + ":" + rule.destinationPort()
        );
    }

    @Override
    public String getTable() {
        return "nat";
    }

    @Override
    public String getBuiltinChain() {
        return "PREROUTING";
    }
}
