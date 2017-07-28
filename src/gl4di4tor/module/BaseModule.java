package gl4di4tor.module;

import java.net.Socket;

/**
 * Created by Gladiator on 7/29/2017 AD.
 */
public abstract class BaseModule implements Runnable {

    protected Socket socket;

    public BaseModule(Socket socket) {
        this.socket = socket;
    }

    public abstract void execute();
}
