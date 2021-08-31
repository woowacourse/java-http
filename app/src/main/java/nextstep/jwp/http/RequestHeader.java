package nextstep.jwp.http;

import java.util.Map;

public class RequestHeader {

    private static final String ACCEPT = "Accept";

    private final Map<String, String> header;

    public RequestHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getParameter(String key) {
        return header.get(key);
    }

    public String acceptType(Uri uri) {
        if (header.containsKey(ACCEPT)) {
            String value = header.get(ACCEPT);
            if (value.contains("css")) {
                return "text/css";
            }
            if (value.contains("javascript") || uri.getResourceUri().endsWith(".js")) {
                return "application/javascript";
            }
        }
        return "text/html";
    }
}
