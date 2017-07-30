package gl4di4tor.engine.proxy;

import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.Router;
import gl4di4tor.engine.BaseEngine;
import gl4di4tor.log.LogService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Gladiator on 7/10/2017 AD.
 */

public class ProxyEngine extends BaseEngine {
    private ServerSocket serverSocket;

    public ProxyEngine(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
//        serverSocket.setSoTimeout(10000);
    }

    public void run() {
        LogService.info("Proxy engine started on port " + serverSocket.getLocalPort() + "");

        while (true) {
            try {
                Socket client = serverSocket.accept();

                LogService.log("Connection received to proxy engine from " + client.getRemoteSocketAddress());

                Router.getInstance().route(client);

            } catch (SocketTimeoutException s) {
                LogService.fatal("Socket timed out!");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
