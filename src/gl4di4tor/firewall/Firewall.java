package gl4di4tor.firewall;

import java.io.IOException;

/**
 * Created by gladiator on 9/3/17.
 */
public interface Firewall {
    public void addProxyServerRules() throws Exception;

    public void addWebServerRules() throws Exception;

    public void clearProxyServerRules() throws Exception;

    public void clearWebServerRules() throws Exception;

    public void flush() throws IOException, InterruptedException;
}
