package io.gl4dius.cli.command;

import io.gl4dius.cli.module.firewall.IptablesRuleManager;
import io.gl4dius.cli.module.ipforwarding.Ipv4ForwardingManager;
import io.gl4dius.cli.module.stealth.NetworkStealthManager;
import io.gl4dius.cli.service.PreferencesService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "Administrative", description = "Administrative commands")
public class AdministrativeCommands {

    private final PreferencesService preferencesService;
    private final List<IptablesRuleManager> iptablesRuleManagers;
    private final Ipv4ForwardingManager ipv4ForwardingManager;
    private final NetworkStealthManager networkStealthManager;

    @Command(name = "config set", description = "Set global configuration")
    public void updatePreferences(
            @Argument(index = 0, description = "Key configuration")
            @NotBlank
            String key,
            @Argument(index = 1, description = "Value to set")
            @NotBlank
            String value
    ) {
        this.preferencesService.updatePreferences(key, value);
    }

    @Command(name = "config show", description = "Show global configuration")
    public Object showPreferences() {
        return this.preferencesService.listPreferences();
    }

    @Command(name = "cleanup", description = "Cleanup leftover system changes")
    public void cleanup() {
        this.iptablesRuleManagers.forEach(IptablesRuleManager::purge);
        this.ipv4ForwardingManager.disableIpv4Forwarding();
        this.networkStealthManager.deactivateStealthMode();
    }
}
