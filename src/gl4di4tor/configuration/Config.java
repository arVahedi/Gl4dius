package gl4di4tor.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by gladiator on 7/11/17.
 */
public class Config {
    private int proxyServerPort;
    private String defacePage;

    private static Config instance;

    private Config() throws IOException {
        load();
    }

    public static Config getInstance() throws IOException {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private void load() throws IOException {
        File configFile = new File("configuration.properties");
        FileReader fileReader = new FileReader(configFile);
        Properties config = new Properties();
        config.load(fileReader);
        this.proxyServerPort = Integer.valueOf(config.getProperty("proxy.server.port"));
        this.defacePage = config.getProperty("deface.page");
    }

    public int getProxyServerPort() {
        return proxyServerPort;
    }

    public void setProxyServerPort(int proxyServerPort) {
        this.proxyServerPort = proxyServerPort;
    }

    public String getDefacePage() {
        return defacePage;
    }

    public void setDefacePage(String defacePage) {
        this.defacePage = defacePage;
    }
}
