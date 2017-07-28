package gl4di4tor.engine.proxy;

import gl4di4tor.Router;
import gl4di4tor.module.deface.DefaceAttack;
import gl4di4tor.log.LogService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Gladiator on 7/10/2017 AD.
 */

public class ProxyEngine extends Thread {
    private ServerSocket serverSocket;

    public ProxyEngine(int port) throws IOException {
        serverSocket = new ServerSocket(port);
//        serverSocket.setSoTimeout(10000);
    }

    public void run() {
        LogService.info("Proxy engine started on port " + serverSocket.getLocalPort() + "");

        while (true) {
            try {
                Socket client = serverSocket.accept();

                LogService.info("Connection received from " + client.getRemoteSocketAddress());

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
