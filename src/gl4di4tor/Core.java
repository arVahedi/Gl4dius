package gl4di4tor;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.configuration.Config;
import gl4di4tor.engine.proxy.ProxyEngine;
import gl4di4tor.log.LogService;

import java.io.IOException;

public class Core {

    public static void main(String[] args) throws IOException {
        try {
            Thread t = new ProxyEngine(Config.getInstance().getProxyServerPort());
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}