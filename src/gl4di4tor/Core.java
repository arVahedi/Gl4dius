package gl4di4tor;

import gl4di4tor.assets.AttackMode;
import gl4di4tor.assets.ErrorCode;
import gl4di4tor.configuration.Config;
import gl4di4tor.engine.proxy.ProxyEngine;
import gl4di4tor.engine.web.WebServerEngine;
import gl4di4tor.firewall.Firewall;
import gl4di4tor.firewall.FirewallFactory;
import gl4di4tor.log.LogService;
import gl4di4tor.utility.security.Authorization;

import java.io.IOException;

public class Core {

    public static void main(String[] args) throws Exception {
        try {
            /*if (!Authorization.isRootUser()) {
                LogService.fatal(ErrorCode.PERMISSION_DENIED.getDescription());
                System.exit(ErrorCode.PERMISSION_DENIED.getValue());
            }*/

            Firewall firewall = FirewallFactory.getFirewallInstance();

            firewall.addProxyServerRules();
            Thread proxyEngine = new ProxyEngine(Config.getInstance().getProxyServerPort());
            proxyEngine.start();

            if (Config.getInstance().getAttackMode() != AttackMode.SNIFF) {
                firewall.addWebServerRules();
                Thread webServerEngine = new WebServerEngine(Config.getInstance().getWebServerPort());
                webServerEngine.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}