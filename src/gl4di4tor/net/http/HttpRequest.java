package gl4di4tor.net.http;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * Created by gladiator on 7/30/17.
 */

public class HttpRequest {

    private String requestLine;
    private String method;
    private String uri;
    private String version;
    private Hashtable<String, String> requestHeaders;
    private StringBuffer messagetBody;

    public HttpRequest(String request) throws Exception {
        requestHeaders = new Hashtable<>();
        messagetBody = new StringBuffer();
        parseRequest(request);
    }

    public void parseRequest(String request) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(request));

        setRequestLine(reader.readLine()); // Request-Line ; Section 5.1

        String header = reader.readLine();
        while (header.length() > 0) {
            appendHeaderParameter(header);
            header = reader.readLine();
        }

        String bodyLine = reader.readLine();
        while (bodyLine != null) {
            appendMessageBody(bodyLine);
            bodyLine = reader.readLine();
        }

    }

    public String getRequestLine() {
        return requestLine;
    }

    private void setRequestLine(String requestLine) throws Exception {
        if (requestLine == null || requestLine.length() == 0) {
            throw new Exception("Invalid Request-Line: " + requestLine);
        }
        this.requestLine = requestLine;

        String[] args = this.requestLine.split(" ");
        this.method = args[0].trim();
        this.uri = args[1].trim();
        this.version = args[2].trim();

    }

    private void appendHeaderParameter(String header) throws Exception {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new Exception("Invalid Header Parameter: " + header);
        }
        requestHeaders.put(header.substring(0, idx), header.substring(idx + 1, header.length()).trim());
    }

    public String getMessageBody() {
        return messagetBody.toString();
    }

    private void appendMessageBody(String bodyLine) {
        messagetBody.append(bodyLine).append("\r\n");
    }

    public String getHeaderParam(String headerName) {
        return requestHeaders.get(headerName);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
