package gl4di4tor.net.http;

/**
 * Created by gladiator on 7/30/17.
 */

public class HttpRequest extends BaseHttpObject {

    private String method;
    private String uri;

    public HttpRequest(String request) throws Exception {
        super();
        parseData(request);
    }

    protected void setRequestLine(String requestLine) throws Exception {
        if (requestLine == null || requestLine.length() == 0) {
            throw new Exception("Invalid Request-Line: " + requestLine);
        }
        this.requestLine = requestLine;

        String[] args = this.requestLine.split(" ");
        this.method = args[0].trim();
        this.uri = args[1].trim();
        this.version = args[2].trim();

    }

    //region Getter and Setter
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
    //endregion
}
