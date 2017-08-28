package gl4di4tor.module;

import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.log.LogService;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Gladiator on 7/29/2017 AD.
 */
public abstract class BaseModule implements Runnable {

    protected Socket socket;

    public BaseModule(Socket socket) {
        this.socket = socket;
    }

    protected int readData(byte[] data) throws IOException {
        InputStream stream = this.socket.getInputStream();
        int dataLen = stream.read(data);
        return dataLen;
    }

    protected void dumpData(byte[] data) {
        LogService.log(new String(data), Ansi.FColor.NONE, Ansi.BColor.NONE);
    }

    protected byte[] removeKeepAliveHeader(byte[] socketData) {
        String dataString = new String(socketData);
        dataString = dataString.replace("Connection: keep-alive", "Connection: close");
        /*if (dataString.contains("Keep-alive:")) {
            System.out.println(dataString);
        }*/
        return dataString.getBytes();
    }

    public abstract void execute();
}
