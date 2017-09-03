package gl4di4tor.firewall;

import gl4di4tor.firewall.iptables.Iptables;
import gl4di4tor.firewall.pf.Pf;
import gl4di4tor.utility.os.OS;

/**
 * Created by gladiator on 9/3/17.
 */
public class FirewallFactory {
    public static Firewall getFirewallInstance() {
        if (OS.isMac()) {
            return new Pf();
        } else {
            return new Iptables();
        }
    }
}
