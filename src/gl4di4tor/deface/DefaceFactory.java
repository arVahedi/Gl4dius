package gl4di4tor.deface;

import gl4di4tor.configuration.Config;

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
public class DefaceFactory {

    private static final Map<String, String> HTTP_HEADER_MAP = new HashMap<>();
    private static final String defacePath = "src/gl4di4tor/deface/pages/";
    private static final String HTTP_END_OF_HEADERS = "\r\n\r\n";

    static {
        HTTP_HEADER_MAP.put("Content-Type", "text/html");
//        HTTP_HEADER_MAP.put("Strict-Transport-Security", "max-age=31536000");
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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

    public static String makeDefaceResponse() throws IOException {
        String defacePage = DefaceFactory.readFile(defacePath + Config.getInstance().getDefacePage(),
                StandardCharsets.UTF_8);
        return makeHttpHeaders() + defacePage.length() + HTTP_END_OF_HEADERS + defacePage;
    }
}
