package io.gl4dius.cli.command;

import io.gl4dius.cli.assets.PoisoningMode;
import io.gl4dius.cli.module.arp.ArpPoisoner;
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

    @Command(name = "arp poison", description = "Start an ARP poisoning")
    public void poisoning(
            @Option(longName = "interface", shortName = 'i', required = true, description = "NIC to poison")
            String interfaceName,
            @Option(longName = "mode", shortName = 'm', defaultValue = "BC", description = "Poisoning mode: BC=BROADCAST, TS=TARGET_SPECIFIC")
            String mode,
            @Option(longName = "target-ip", shortName = 't', description = "Target IP (only used in TS mode)")
            String targetIp,
            @Option(longName = "target-mac", description = "Target MAC-Address (only used in TS mode)")
            String targetMac,
            @Option(longName = "spoof", shortName = 's', required = true, description = "Spoofed IP")
            String spoofIp
    ) throws Exception {
        var poisoningMode = PoisoningMode.fromAcronym(mode);
        this.arpPoisoner.poison(interfaceName, poisoningMode, spoofIp, targetIp, targetMac);
    }
}
