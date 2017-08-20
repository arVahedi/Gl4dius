package gl4di4tor.net.http;

/**
 * Created by gladiator on 8/15/17.
 */
public class HttpResponse extends BaseHttpObject {
    private int code;
    private String description;

    public HttpResponse(String response) throws Exception {
        super();
        parseData(response);
    }

    @Override
    protected void setRequestLine(String requestLine) throws Exception {
        if (requestLine == null || requestLine.length() == 0) {
            throw new Exception("Invalid Request-Line: " + requestLine);
        }
        this.requestLine = requestLine;

        String[] args = this.requestLine.split(" ");
        this.version = args[0].trim();
        this.code = Integer.valueOf(args[1].trim());
        this.description = "";
        for (int i = 2; i < args.length; i++) {
            if (!this.description.equalsIgnoreCase("")) {
                this.description = this.description + " ";
            }
            this.description = this.description + args[i].trim();
        }
    }

    //region Getter and Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
