package gl4di4tor.firewall.iptables;

import gl4di4tor.configuration.Config;
import gl4di4tor.firewall.Firewall;
import gl4di4tor.utility.os.OSExecutor;

import java.io.IOException;

/**
 * Created by gladiator on 9/3/17.
 */
public class Iptables implements Firewall {

    private static final String PROXY_SERVER_RULE =
            "iptables -t nat -A PREROUTING -i %s -p tcp --dport 80 -j DNAT --to-destination %s:%s";
    private static final String WEB_SERVER_RULE =
            "iptables -t nat -A PREROUTING -i %s -p tcp --dport 4444 -j DNAT --to-destination %s:%s";
    private static final String CLEAR_RULE =
            "iptables -t nat -F";

    @Override
    public void addProxyServerRules() throws Exception {
        String output = OSExecutor.execute(String.format(PROXY_SERVER_RULE,
                Config.getInstance().getServerNIC(),
                Config.getInstance().getServerIP(),
                Config.getInstance().getProxyServerPort()));
    }

    @Override
    public void addWebServerRules() throws Exception {
        String output = OSExecutor.execute(String.format(WEB_SERVER_RULE,
                Config.getInstance().getServerNIC(),
                Config.getInstance().getServerIP(),
                Config.getInstance().getWebServerPort()));
    }

    @Override
    public void clearRules() throws IOException, InterruptedException {
        OSExecutor.execute(CLEAR_RULE);
    }
}
