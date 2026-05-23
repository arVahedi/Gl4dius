package io.gl4dius.cli.service.attack;

import io.gl4dius.cli.module.arp.ArpPoisoner;
import io.gl4dius.cli.service.DaemonModuleExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefacingService {

    private final DaemonModuleExecutor daemonModuleExecutor;
    private final ArpPoisoner arpPoisoner;

    public void launch(String nicName, String spoofIp, String targetIp, String targetMac) {
        //Config iptables rules

        this.daemonModuleExecutor.execute("arp-poisoner",
                () -> this.arpPoisoner.poison(nicName, spoofIp, targetIp, targetMac));

        log.debug("[!] Target Locked and Loaded...");

        //Start webserver

        log.debug("Defacing started on interface {} to spoof {}", nicName, spoofIp);
    }
}
