package io.gl4dius.cli.module.firewall;

import io.gl4dius.cli.model.dto.system.CommandRequest;
import io.gl4dius.cli.model.dto.system.CommandResult;
import io.gl4dius.cli.service.SystemCommandExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class IptablesRuleManager {

    protected static final String GL4DIUS_CHAIN_PREFIX = "GL4DIUS_CHAIN";
    private static final Duration IPTABLES_TIMEOUT = Duration.ofSeconds(5);
    private static final int MAX_DUPLICATE_JUMP_RULES = 100;

    private final SystemCommandExecutor commandExecutor;

    @PostConstruct
    public void initialize() {
        createChainIfMissing();

        if (!jumpExists()) {
            executeOrThrow(List.of("iptables", "-t", getTable(), "-A", getBuiltinChain(), "-j", getChain()));
        }
    }

    @PreDestroy
    public void destroy() {
        purge();
    }

    public void purge() {
        if (!chainExists()) {
            return;
        }

        var deleteJumpRule = List.of("iptables", "-t", getTable(), "-D", getBuiltinChain(), "-j", getChain());

        // 1. Remove the jump/reference from the built-in chain
        int deleted = 0;
        while (execute(deleteJumpRule)) {
            deleted++;

            if (deleted > MAX_DUPLICATE_JUMP_RULES) {
                throw new IllegalStateException("Too many duplicate jump rules from %s to %s. Aborting cleanup."
                        .formatted(getBuiltinChain(), getChain()));
            }
            // keep deleting duplicate jump rules
        }
        // 2. Remove all rules inside your custom chain
        executeIgnoringFailure(List.of("iptables", "-t", getTable(), "-F", getChain()));
        // 3. Delete the now-empty custom chain
        executeIgnoringFailure(List.of("iptables", "-t", getTable(), "-X", getChain()));
    }

    public void flushRules() {
        if (!chainExists()) {
            return;
        }

        // Remove all rules inside your custom chain
        executeIgnoringFailure(List.of("iptables", "-t", getTable(), "-F", getChain()));
    }

    protected void executeOrThrow(List<String> command) {
        commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT))
                .requireStdout();
    }

    protected void executeIgnoringFailure(List<String> command) {
        commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT));
    }

    protected boolean execute(List<String> command) {
        CommandResult result = commandExecutor.execute(new CommandRequest(command, IPTABLES_TIMEOUT));
        return result.succeeded();
    }

    private void createChainIfMissing() {
        if (chainExists()) {
            return;
        }

        executeOrThrow(List.of("iptables", "-t", getTable(), "-N", getChain()));
    }

    private boolean chainExists() {
        return execute(List.of("iptables", "-t", getTable(), "-L", getChain(), "-n"));
    }

    private boolean jumpExists() {
        return execute(List.of("iptables", "-t", getTable(), "-C", getBuiltinChain(), "-j", getChain()));
    }

    public String getTable() {
        return "filter";
    }

    public String getChain() {
        return GL4DIUS_CHAIN_PREFIX + "_" + getBuiltinChain();
    }

    public abstract String getBuiltinChain();
}
