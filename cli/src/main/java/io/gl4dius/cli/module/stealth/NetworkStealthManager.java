package io.gl4dius.cli.module.stealth;

import io.gl4dius.cli.module.firewall.IptablesInputManager;
import io.gl4dius.cli.module.firewall.IptablesOutputManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetworkStealthManager {

    private final IptablesInputManager iptablesInputManager;
    private final IptablesOutputManager iptablesOutputManager;

    public void activateStealthMode() {
        this.iptablesInputManager.blockUdpTraceroute();
        this.iptablesOutputManager.blockIcmp();
    }

    public void deactivateStealthMode() {
        this.iptablesInputManager.flushRules();
        this.iptablesOutputManager.flushRules();
    }
}
