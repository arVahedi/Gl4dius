package gl4di4tor.net;

import gl4di4tor.log.LogService;

import java.io.*;
import java.net.Socket;

/**
 * Created by gladiator on 7/31/17.
 */
public class OutgoingChannel extends Thread {
    private Socket clientSocket;
    private final String SERVER_URL;
    private final int SERVER_PORT;
    private byte[] clientSocketData = null;
    private int clientSocketDataLen;

    public OutgoingChannel(String host, Socket clientSocket, byte[] clientSocketData, int clientSocketDataLen) {
        String[] tmp = host.split(":");
        this.SERVER_URL = tmp[0];
        this.SERVER_PORT = tmp.length > 1 ? Integer.valueOf(tmp[1]) : 80;
        this.clientSocket = clientSocket;
        this.clientSocketData = clientSocketData;
        this.clientSocketDataLen = clientSocketDataLen;

        this.start();
    }

    @Override
    public void run() {
        try {

            if (this.SERVER_URL == null || this.SERVER_URL.equalsIgnoreCase("")) {
                LogService.debug("Server url is null in outgoing channel.");
                return;
            }

            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = clientSocket.getInputStream();
            final OutputStream outToClient = clientSocket.getOutputStream();
            Socket client = null;
            Socket server;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        if (clientSocketData == null) {
                            while ((bytes_read = inFromClient.read(request)) != -1) {
                                outToServer.write(request, 0, bytes_read);
                                outToServer.flush();
                                //CREATE YOUR LOGIC HERE
                            }
                        } else {
                            outToServer.write(clientSocketData, 0, clientSocketDataLen);
                            outToServer.flush();
                        }
                    } catch (IOException e) {
                    }
                    /*try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
