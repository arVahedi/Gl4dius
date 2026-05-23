package io.gl4dius.cli.service.attack;

import io.gl4dius.cli.Gl4diusApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MitMLauncher {

    private final DefacingService defacingService;
    private final SniffingService sniffingService;
    private final PhishingService phishingService;

    public void launch(String nicName, String spoofIp, String targetIp, String targetMac) {
        var sessionConfig = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"))
                .getConfig();

        switch (sessionConfig.mode()) {
            case DEFACING -> this.defacingService.launch(nicName, spoofIp, targetIp, targetMac);
            case SNIFFING -> this.sniffingService.launch();
            case PHISHING -> this.phishingService.launch();
        }
    }
}
