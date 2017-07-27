package gl4di4tor.engine.proxy;

import gl4di4tor.deface.DefaceFactory;

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
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

        while (true) {
            try {
                Socket server = serverSocket.accept();

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());

                out.writeUTF(DefaceFactory.makeDefaceResponse());
                out.flush();
                server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
