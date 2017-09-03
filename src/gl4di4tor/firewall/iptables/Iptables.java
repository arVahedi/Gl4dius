package gl4di4tor.firewall.iptables;

import gl4di4tor.firewall.Firewall;

/**
 * Created by gladiator on 9/3/17.
 */
public class Iptables implements Firewall {

    private static String proxyServerRule =
            "iptables -t nat -A PREROUTING -i %s -p tcp --dport 80 -j DNAT --to-destination %s:%s";
    private static String webServerRule =
            "iptables -t nat -A PREROUTING -i %s -p tcp --dport 4444 -j DNAT --to-destination %s:%s";

    @Override
    public void addProxyServerRules() {

    }

    @Override
    public void addWebServerRules() {

    }
}
