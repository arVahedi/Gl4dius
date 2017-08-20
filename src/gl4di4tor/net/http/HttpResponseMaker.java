package gl4di4tor.net.http;

import gl4di4tor.configuration.Config;
import gl4di4tor.log.LogService;
import gl4di4tor.utility.file.FileUtility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gladiator on 7/11/17.
 */
public class HttpResponseMaker {

    private static final Map<String, String> HTTP_HEADER_MAP = new HashMap<>();
    private static final String HTTP_END_OF_HEADERS = "\r\n\r\n";

    static {
        HTTP_HEADER_MAP.put("Content-Type", "text/html");
//        HTTP_HEADER_MAP.put("Strict-Transport-Security", "max-age=31536000");
    }

    private static String makeHttpHeaders() {
        StringBuilder stringBuilder = new StringBuilder("HTTP/1.1 200 OK\r\n");
        HTTP_HEADER_MAP.forEach((key, value) -> {
            stringBuilder.append(key);
            stringBuilder.append(":");
            stringBuilder.append(value);
            stringBuilder.append("\r\n");
        });
        stringBuilder.append("Content-Length:");

        return stringBuilder.toString();
    }

    public static String makeHttpResponse(String path) throws Exception {
        LogService.debug("Making response from " + path);
        String defacePage = FileUtility.readFile(path,
                StandardCharsets.UTF_8);
        return makeHttpHeaders() + defacePage.length() + HTTP_END_OF_HEADERS + defacePage;
    }
}
