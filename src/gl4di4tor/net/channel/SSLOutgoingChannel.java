package gl4di4tor.net.channel;

import gl4di4tor.log.LogService;
import org.apache.commons.lang3.ArrayUtils;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gladiator on 8/20/17.
 */
public class SSLOutgoingChannel extends BaseOutgoingChannel {

    public SSLOutgoingChannel(String host, byte[] clientSocketData, int clientSocketDataLen) {
        super();
        String[] tmp = host.split(":");

        if (host.toLowerCase().startsWith("http://") || host.toLowerCase().startsWith("https://")) {
            this.serverUrl = tmp[1].substring(2);
            this.serverPort = tmp.length > 2 ? Integer.valueOf(tmp[2]) : 443;
        } else {
            this.serverUrl = tmp[0];
            this.serverPort = tmp.length > 1 ? Integer.valueOf(tmp[1]) : 443;
        }
        this.serverUrl = this.serverUrl.substring(0, this.serverUrl.indexOf("/"));
        if (this.serverUrl.endsWith("/")) {
            this.serverUrl = this.serverUrl.substring(0, this.serverUrl.length() - 1);
        }

        this.clientSocketData = clientSocketData;
        this.clientSocketDataLen = clientSocketDataLen;
        this.channelMode = ChannelMode.BLOCKING;
    }

    @Override
    public void run() {

    }

    @Override
    public byte[] execute() {
        if (this.channelMode == ChannelMode.BLOCKING) {
            return executeBlocking();
        } else {
            LogService.error("Not recognize outgoing channel mode : " + String.valueOf(this.channelMode));
        }
        return null;
    }

    private byte[] executeBlocking() {
        try {
            if (this.serverUrl == null || this.serverUrl.equalsIgnoreCase("")) {
                LogService.debug("Server url is null in outgoing channel.");
                return null;
            }

            byte[] reply = new byte[4096];


//            System.setProperty("javax.net.ssl.trustStore", "clienttrust");
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket server;
            try {
                server = ssf.createSocket(this.serverUrl, this.serverPort);
                server.setKeepAlive(false);
            } catch (IOException e) {
                LogService.error("Can not connect to " + this.serverUrl + ":" + this.serverPort + " on SSL socket.");
                throw new RuntimeException(e);
            }

            final BufferedInputStream inFromServer = new BufferedInputStream(server.getInputStream());
            final BufferedOutputStream outToServer = new BufferedOutputStream(server.getOutputStream());

            new Thread() {
                public void run() {
                    int clientBytesRead;
                    try {
                        outToServer.write(clientSocketData, 0, clientSocketDataLen);
                        outToServer.flush();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
