package gl4di4tor;

import gl4di4tor.configuration.Config;
import gl4di4tor.engine.proxy.ProxyEngine;
import gl4di4tor.engine.web.WebServerEngine;

import java.io.IOException;

public class Core {

    public static void main(String[] args) throws Exception {
        try {
            Thread proxyEngine = new ProxyEngine(Config.getInstance().getProxyServerPort());
            proxyEngine.start();

            Thread webServerEngine = new WebServerEngine(Config.getInstance().getWebServerPort());
            webServerEngine.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}