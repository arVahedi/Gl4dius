package gl4di4tor.engine.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gl4di4tor.engine.BaseEngine;
import gl4di4tor.log.LogService;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by gladiator on 7/29/17.
 */
public class WebServerEngine extends BaseEngine {

    private HttpServer httpServer;

    public WebServerEngine(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/", new Handler());
    }

    @Override
    public void run() {
        LogService.info("Web server engine started on port " + this.httpServer.getAddress().getPort());
        this.httpServer.start();
    }

    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            LogService.log("HTTP request received from " + httpExchange.getRemoteAddress() + " for resource " +
                    httpExchange.getRequestURI().getPath());
            System.out.println(httpExchange.getLocalAddress().getPort());
            new Thread(new WebServerHandler(httpExchange)).start();
        }
    }
}
