package gl4di4tor.engine.web;

import com.diogonunes.jcdp.color.api.Ansi;
import com.sun.net.httpserver.HttpExchange;
import gl4di4tor.log.LogService;
import gl4di4tor.utility.file.FileUtility;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * Created by gladiator on 7/29/17.
 */
public class WebServerHandler implements Runnable {
    private HttpExchange httpExchange;
    private final static String WWW_PATH = "www";

    public WebServerHandler(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public void run() {
        LogService.debug("Processing request in web server for " + this.httpExchange.getRemoteAddress());
        handle();
        LogService.debug("request from " + this.httpExchange.getRemoteAddress() + " processed.");
    }

    private void handle() {
        String resourcePath = WWW_PATH + this.httpExchange.getRequestURI().getPath();
        if (!isValidResource(resourcePath)) {
            LogService.log("BE CAREFUL!! Web server detected a request for a invalid resource. Maybe you are under LFI attack.",
                    Ansi.FColor.RED, Ansi.BColor.YELLOW);
            this.httpExchange.close();
            return;
        }
        try {
            File file = new File(resourcePath);
            this.httpExchange.sendResponseHeaders(200, file.length());
            OutputStream os = this.httpExchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            LogService.debug(resourcePath + " sent to " + this.httpExchange.getRemoteAddress() + " successfully.");
        } catch (NoSuchFileException ex) {
            LogService.error(resourcePath + " not find.");
        } catch (Exception ex) {
            LogService.error(ex);
            ex.printStackTrace();
        }
    }

    private boolean isValidResource(String resource) {
        if (resource.contains("..") || resource.contains("%")) {
            return false;
        }
        return true;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }
}
