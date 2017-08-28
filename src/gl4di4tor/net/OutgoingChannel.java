package gl4di4tor.net;

import gl4di4tor.log.LogService;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gladiator on 7/31/17.
 */
public class OutgoingChannel extends BaseOutgoingChannel {

    public OutgoingChannel(String host, Socket clientSocket, byte[] clientSocketData, int clientSocketDataLen,
                           ChannelMode channelMode) {
        super();
        String[] tmp = host.split(":");

        if (host.toLowerCase().startsWith("http://") || host.toLowerCase().startsWith("https://")) {
            this.serverUrl = tmp[1].substring(2);
            this.serverPort = tmp.length > 2 ? Integer.valueOf(tmp[2]) : 80;
        } else {
            this.serverUrl = tmp[0];
            this.serverPort = tmp.length > 1 ? Integer.valueOf(tmp[1]) : 80;
        }
        if (this.serverUrl.endsWith("/")) {
            this.serverUrl = this.serverUrl.substring(0, this.serverUrl.length() - 1);
        }

        this.clientSocket = clientSocket;
        this.clientSocketData = clientSocketData;
        this.clientSocketDataLen = clientSocketDataLen;
        this.channelMode = channelMode;
    }

    @Override
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
            if (this.serverUrl == null || this.serverUrl.equalsIgnoreCase("")) {
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
                server = new Socket(this.serverUrl, this.serverPort);
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
            if (this.serverUrl == null || this.serverUrl.equalsIgnoreCase("")) {
                LogService.debug("Server url is null in outgoing channel.");
                return null;
            }

            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = clientSocket.getInputStream();
            Socket server;
            // connects a socket to the server
            try {
                server = new Socket(this.serverUrl, this.serverPort);
                server.setKeepAlive(false);
            } catch (IOException e) {
                LogService.error("Can not connect to " + this.serverUrl + ":" + this.serverPort);
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final BufferedInputStream inFromServer = new BufferedInputStream(server.getInputStream());
            final BufferedOutputStream outToServer = new BufferedOutputStream(server.getOutputStream());
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
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
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int serverBytesRead;
            List<Byte> serverResponse = new ArrayList<>();
            try {
                while ((serverBytesRead = inFromServer.read(reply)) > 0) {
                    for (int counter = 0; counter < serverBytesRead; counter++) {
                        serverResponse.add(reply[counter]);
                    }
                }
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
