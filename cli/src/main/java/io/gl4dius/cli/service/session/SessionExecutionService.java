package io.gl4dius.cli.service.session;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.assets.PreferencesKey;
import io.gl4dius.cli.model.dto.iptables.IptablesRedirectRule;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.module.arp.ArpPoisoner;
import io.gl4dius.cli.module.firewall.IptablesRuleManager;
import io.gl4dius.cli.module.ipforwarding.Ipv4ForwardingManager;
import io.gl4dius.cli.repository.PreferencesRepository;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.service.DaemonModuleExecutor;
import io.gl4dius.cli.service.proxy.ProxyServerEngine;
import io.gl4dius.cli.utility.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionExecutionService {

    private final DaemonModuleExecutor daemonModuleExecutor;
    private final SessionRepository sessionRepository;
    private final Ipv4ForwardingManager ipv4ForwardingManager;
    private final IptablesRuleManager iptablesRuleManager;
    private final ArpPoisoner arpPoisoner;
    private final PreferencesRepository preferencesRepository;
    private final ProxyServerEngine proxyServerEngine;

    public void switchSession(String identifier) {
        var session = UuidUtil.parseUuidIfValid(identifier)
                .flatMap(this.sessionRepository::findById)
                .or(() -> this.sessionRepository.findByName(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Session %s not found".formatted(identifier)));

        exitSession();
        Gl4diusApplication.setCurrentSession(session);
        log.debug("Switched to session {}", session.getId());
    }

    public void exitSession() {
        Gl4diusApplication.getCurrentSession().ifPresent(session -> {
            log.debug("Exiting session {}", session.getId());
            stopSession();
        });
        Gl4diusApplication.setCurrentSession(null);
    }

    public void startSession(String nicName, String spoofIp, String targetIp, String targetMac) {
        //Start attacking infrastructure to capture traffic:
        //    - Starting Proxy server
        //    - Enabling IP forwarding
        //    - Set Iptables rules
        //    - Starting ARP Poisoning

        var proxyPort = this.preferencesRepository.findById(PreferencesKey.PROXY_SERVER_PORT)
                .orElseThrow(() -> new IllegalArgumentException("Proxy server port has not been set"))
                .getValue();
        this.proxyServerEngine.start("127.0.0.1", Integer.parseInt(proxyPort));

        this.ipv4ForwardingManager.enableIpv4Forwarding();

        this.iptablesRuleManager.addRedirectRule(IptablesRedirectRule.builder()
                .inputInterface(nicName)
                .originalPort(80)
                .destinationIp("127.0.0.1")
                .destinationPort(Integer.parseInt(proxyPort))
                .build());

        this.daemonModuleExecutor.execute("arp-poisoner",
                () -> this.arpPoisoner.poison(nicName, spoofIp, targetIp, targetMac));

        log.debug("[!] Target Locked and Loaded...");
    }

    public void stopSession() {
        var session = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Current session not set"));
        stopSession(session);
    }

    public void stopSession(@NonNull Session session) {
        log.debug("Stopping session {}", session.getId());
        this.daemonModuleExecutor.stop(session.getId());
    }
}
