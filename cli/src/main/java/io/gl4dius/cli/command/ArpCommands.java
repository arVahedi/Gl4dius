package io.gl4dius.cli.command;

import io.gl4dius.cli.module.arp.ArpPoisoner;
import io.gl4dius.cli.service.DaemonModuleExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@CommandGroup(name = "ARP", description = "ARP commands")
public class ArpCommands {

    private final ArpPoisoner arpPoisoner;
    private final DaemonModuleExecutor daemonModuleExecutor;

    @Command(name = "arp poison", description = "Start an ARP poisoning")
    public String poisoning(
            @Option(longName = "interface", shortName = 'i', required = true, description = "NIC to poison")
            String interfaceName,
            @Option(longName = "target-ip", shortName = 't', description = "Target IP (if not specified, ALL clients in the LAN will be poisoned)")
            String targetIp,
            @Option(longName = "target-mac", description = "Target MAC-Address")
            String targetMac,
            @Option(longName = "spoof", shortName = 's', description = "Spoofed IP (default NIC gateway IP)")
            String spoofIp,
            @Option(longName = "daemon", shortName = 'd', description = "Run as Daemon", defaultValue = "false")
            boolean daemon
    ) {
        if (daemon) {
            this.daemonModuleExecutor.execute("arp-poisoner",
                    () -> this.arpPoisoner.poison(interfaceName, spoofIp, targetIp, targetMac));
        } else {
            this.arpPoisoner.poison(interfaceName, spoofIp, targetIp, targetMac);
        }

        return "ARP poisoning started on interface " + interfaceName + " to spoof " + spoofIp;
    }
}
