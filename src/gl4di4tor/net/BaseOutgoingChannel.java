package gl4di4tor.net;

import java.net.Socket;

/**
 * Created by gladiator on 8/20/17.
 */
public abstract class BaseOutgoingChannel extends Thread {
    protected Socket clientSocket;
    protected String serverUrl;
    protected int serverPort;
    protected byte[] clientSocketData = null;
    protected int clientSocketDataLen;
    protected ChannelMode channelMode = ChannelMode.NON_BLOCKING;

    public enum ChannelMode {
        BLOCKING,
        NON_BLOCKING
    }

    public abstract byte[] execute();
}
