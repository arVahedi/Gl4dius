package gl4di4tor.module.deface;

import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.module.BaseModule;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Gladiator on 7/29/2017 AD.
 */
public class DefaceModule extends BaseModule {


    public DefaceModule(Socket socket) {
        super(socket);
    }

    @Override
    public void execute() {
        try {
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());

            out.writeUTF(DefaceAttack.makeDefaceResponse());
            out.flush();
            this.socket.close();
        } catch (Exception e) {
            LogService.error(e);
        }
    }

    @Override
    public void run() {
        try {
            LogService.info("Deface attack launch for " + this.socket.getRemoteSocketAddress() + " using deface page : " +
                    Config.getInstance().getDefacePage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        execute();
    }
}
