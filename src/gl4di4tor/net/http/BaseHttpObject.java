package gl4di4tor.net.http;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * Created by gladiator on 8/15/17.
 */
public abstract class BaseHttpObject {
    protected String requestLine;
    protected Hashtable<String, String> requestHeaders;
    protected StringBuffer messageBody;
    protected String version;

    public BaseHttpObject() {
        this.requestHeaders = new Hashtable<>();
        this.messageBody = new StringBuffer();
    }

    public void parseData(String data) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(data));

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

    private void appendHeaderParameter(String header) throws Exception {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new Exception("Invalid Header Parameter: " + header);
        }
        requestHeaders.put(header.substring(0, idx), header.substring(idx + 1, header.length()).trim());
    }

    private void appendMessageBody(String bodyLine) {
        messageBody.append(bodyLine).append("\r\n");
    }

    public String getHeaderParam(String headerName) {
        return requestHeaders.get(headerName);
    }

    //region Getter and Setter
    public String getRequestLine() {
        return requestLine;
    }

    protected abstract void setRequestLine(String requestLine) throws Exception;

    public Hashtable<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Hashtable<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getMessageBody() {
        return messageBody.toString();
    }

    public void setMessageBody(StringBuffer messageBody) {
        this.messageBody = messageBody;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    //endregion
}
