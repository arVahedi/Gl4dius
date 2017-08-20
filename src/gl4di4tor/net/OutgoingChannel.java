package gl4di4tor.net;

import gl4di4tor.log.LogService;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gladiator on 7/31/17.
 */
public class OutgoingChannel extends Thread {
    private Socket clientSocket;
    private final String SERVER_URL;
    private final int SERVER_PORT;
    private byte[] clientSocketData = null;
    private int clientSocketDataLen;
    private ChannelMode channelMode = ChannelMode.NON_BLOCKING;

    public enum ChannelMode {
        BLOCKING,
        NON_BLOCKING;
    }

    public OutgoingChannel(String host, Socket clientSocket, byte[] clientSocketData, int clientSocketDataLen,
                           ChannelMode channelMode) {
        String[] tmp = host.split(":");
        this.SERVER_URL = tmp[0];
        this.SERVER_PORT = tmp.length > 1 ? Integer.valueOf(tmp[1]) : 80;
        this.clientSocket = clientSocket;
        this.clientSocketData = clientSocketData;
        this.clientSocketDataLen = clientSocketDataLen;
        this.channelMode = channelMode;
    }

    public byte[] execute() {
        if (this.channelMode == ChannelMode.NON_BLOCKING) {
            this.start();
        } else if (this.channelMode == ChannelMode.BLOCKING) {
            return executeBlocking();
        } else {
            LogService.error("Not recognize outgoing channel mode : " + String.valueOf(this.channelMode));
        }
        return null;
    }

    @Override
    public void run() {
        executeNonBlocking();
    }

    private void executeNonBlocking() {
        try {
            if (this.SERVER_URL == null || this.SERVER_URL.equalsIgnoreCase("")) {
                LogService.debug("Server url is null in outgoing channel.");
                return;
            }

            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = this.clientSocket.getInputStream();
            final OutputStream outToClient = this.clientSocket.getOutputStream();
            Socket client = null;
            Socket server;
            // connects a socket to the server
            try {
                server = new Socket(this.SERVER_URL, this.SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final BufferedInputStream inFromServer = new BufferedInputStream(server.getInputStream());
            final BufferedOutputStream outToServer = new BufferedOutputStream(server.getOutputStream());
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytesRead;
                    try {
                        if (clientSocketData == null) {
                            while ((bytesRead = inFromClient.read(request)) != -1) {
                                outToServer.write(request, 0, bytesRead);
                                outToServer.flush();
                                //CREATE YOUR LOGIC HERE
                            }
                        } else {
                            outToServer.write(clientSocketData, 0, clientSocketDataLen);
                            outToServer.flush();
                        }
                    } catch (IOException e) {
                        //ignore me
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytesRead;
            try {
                LogService.info(this.getName() + " - IN WHILE");
                while ((bytesRead = inFromServer.read(reply)) != -1) {
                    LogService.info(this.getName() + " READ : " + bytesRead);
                    outToClient.write(reply, 0, bytesRead);
                    outToClient.flush();
                    //CREATE YOUR LOGIC HERE
                }
                LogService.info(this.getName() + " - OUT WHILE");
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

    private byte[] executeBlocking() {
        try {
            if (this.SERVER_URL == null || this.SERVER_URL.equalsIgnoreCase("")) {
                LogService.debug("Server url is null in outgoing channel.");
                return null;
            }

            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = clientSocket.getInputStream();
            Socket server;
            // connects a socket to the server
            try {
                server = new Socket(this.SERVER_URL, this.SERVER_PORT);
                server.setKeepAlive(false);
            } catch (IOException e) {
                LogService.error("Can not connect to " + this.SERVER_URL + ":" + this.SERVER_PORT);
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final BufferedInputStream inFromServer = new BufferedInputStream(server.getInputStream());
            final BufferedOutputStream outToServer = new BufferedOutputStream(server.getOutputStream());
            // a new thread for uploading to the server
            /*new Thread() {
                public void run() {*/
                    int clientBytesRead;
                    try {
                        if (clientSocketData == null) {
                            while ((clientBytesRead = inFromClient.read(request)) != -1) {
                                outToServer.write(request, 0, clientBytesRead);
                                outToServer.flush();
                                //CREATE YOUR LOGIC HERE
                            }
                        } else {
                            outToServer.write(clientSocketData, 0, clientSocketDataLen);
                            outToServer.flush();
                        }
                    } catch (IOException e) {
                        //ignore me
                    }
                /*}
            }.start();*/
            // current thread manages streams from server to client (DOWNLOAD)
            int serverBytesRead;
            List<Byte> serverResponse = new ArrayList<>();
            try {
                LogService.info(this.getName() + " - IN WHILE");
                while ((serverBytesRead = inFromServer.read(reply)) > 0) {
                    LogService.info(this.getName() + " READ : " + serverBytesRead);
                    for (int counter = 0; counter < serverBytesRead; counter++) {
//                        System.out.print(String.format("%02x", reply[counter]) + " ");
                        serverResponse.add(reply[counter]);
                    }
//                    System.out.print("\n");
                }
                LogService.info(this.getName() + " - OUT WHILE");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Byte[] result = serverResponse.toArray(new Byte[serverResponse.size()]);
            return ArrayUtils.toPrimitive(result);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
