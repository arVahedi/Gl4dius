package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.model.dto.iptables.IptablesRedirectRule;
import io.gl4dius.cli.model.dto.system.CommandRequest;
import io.gl4dius.cli.model.dto.system.CommandResult;
import io.gl4dius.cli.service.SystemCommandExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IptablesRuleManager {

    private static final String TABLE = "nat";
    private static final String GL4DIUS_CHAIN = "GL4DIUS_PREROUTING";
    private static final Duration IPTABLES_TIMEOUT = Duration.ofSeconds(5);
    private static final int MAX_DUPLICATE_JUMP_RULES = 100;

    private final SystemCommandExecutor commandExecutor;

    @PostConstruct
    public void initialize() {
        createChainIfMissing();

        if (!jumpExists()) {
            executeOrThrow(List.of("iptables", "-t", TABLE, "-A", "PREROUTING", "-j", GL4DIUS_CHAIN));
        }
    }

    @PreDestroy
    public void destroy() {
        purge();
    }

    public void addPostRoutingRule(@NonNull String nic) {
        executeOrThrow(List.of("iptables", "-t", TABLE, "-A", "POSTROUTING", "-o", nic, "-j", "MASQUERADE"));
    }

    public void addForwardRule(@NonNull String input, @NonNull String output) {
        executeOrThrow(List.of("iptables", "-A", "FORWARD", "-i", input, "-o", output, "-j", "ACCEPT"));
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
                "-t", TABLE,
                "-D", GL4DIUS_CHAIN,
                "-i", rule.inputInterface(),
                "-p", "tcp",
                "--dport", String.valueOf(rule.originalPort()),
                "-j", "DNAT",
                "--to-destination", rule.destinationIp() + ":" + rule.destinationPort()
        ));
    }

    public void flushRules() {
        // Remove all rules inside your custom chain
        executeIgnoringFailure(List.of("iptables", "-t", TABLE, "-F", GL4DIUS_CHAIN));
    }

    public void purge() {
        var deleteJumpRule = List.of("iptables", "-t", TABLE, "-D", "PREROUTING", "-j", GL4DIUS_CHAIN);

        // 1. Remove the jump/reference from the built-in chain
        int deleted = 0;
        while (execute(deleteJumpRule)) {
            deleted++;

            if (deleted > MAX_DUPLICATE_JUMP_RULES) {
                throw new IllegalStateException("Too many duplicate jump rules from PREROUTING to %s. Aborting cleanup."
                        .formatted(GL4DIUS_CHAIN));
            }
            // keep deleting duplicate jump rules
        }
        // 2. Remove all rules inside your custom chain
        executeIgnoringFailure(List.of("iptables", "-t", TABLE, "-F", GL4DIUS_CHAIN));
        // 3. Delete the now-empty custom chain
        executeIgnoringFailure(List.of("iptables", "-t", TABLE, "-X", GL4DIUS_CHAIN));
    }

    private void createChainIfMissing() {
        if (chainExists()) {
            return;
        }

        executeOrThrow(List.of("iptables", "-t", TABLE, "-N", GL4DIUS_CHAIN));
    }

    private boolean chainExists() {
        return execute(List.of("iptables", "-t", TABLE, "-L", GL4DIUS_CHAIN, "-n"));
    }

    private boolean jumpExists() {
        return execute(List.of("iptables", "-t", TABLE, "-C", "PREROUTING", "-j", GL4DIUS_CHAIN));
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
                "-t", TABLE,
                operation,
                GL4DIUS_CHAIN,
                "-i", rule.inputInterface(),
                "-p", "tcp",
                "--dport", String.valueOf(rule.originalPort()),
                "-j", "DNAT",
                "--to-destination", rule.destinationIp() + ":" + rule.destinationPort()
        );
    }

    private void executeOrThrow(List<String> command) {
        commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT))
                .requireStdout();
    }

    private void executeIgnoringFailure(List<String> command) {
        commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT));
    }

    private boolean execute(List<String> command) {
        CommandResult result = commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT));
        return result.succeeded();
    }

}
