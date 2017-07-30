package gl4di4tor.configuration;

import gl4di4tor.assets.AttackMode;
import gl4di4tor.log.LogService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by gladiator on 7/11/17.
 */
public class Config {
    private int proxyServerPort;
    private int webServerPort;
    private String defacePage;
    private int logLevel;
    private String mode;

    private static Config instance;

    private Config() throws Exception {
        load();
    }

    public static Config getInstance() throws Exception {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private void load() throws Exception {
        File configFile = new File("Gl4dius.properties");
        FileReader fileReader = new FileReader(configFile);
        Properties config = new Properties();
        config.load(fileReader);
        this.proxyServerPort = Integer.valueOf(config.getProperty("proxy.server.port"));
        this.webServerPort = Integer.valueOf(config.getProperty("web.server.port"));
        this.defacePage = config.getProperty("deface.page");
        this.logLevel = Integer.valueOf(config.getProperty("log.level"));
        this.mode = config.getProperty("mode").toUpperCase();

        validateConfiguration();
    }

    private void validateConfiguration() {
        try {
            AttackMode.valueOf(this.mode);
        } catch (Exception ex) {
            LogService.fatal("Config file is invalid. invalid value for mode property", false);
            throw ex;
        }
    }

    //region Getter and Setter
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

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getWebServerPort() {
        return webServerPort;
    }

    public void setWebServerPort(int webServerPort) {
        this.webServerPort = webServerPort;
    }
    //endregion
}
