package gl4di4tor;

import gl4di4tor.configuration.Config;
import gl4di4tor.engine.proxy.ProxyEngine;

import java.io.IOException;

public class Core {

    public static void main(String[] args) throws Exception {
        try {
            Thread t = new ProxyEngine(Config.getInstance().getProxyServerPort());
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}