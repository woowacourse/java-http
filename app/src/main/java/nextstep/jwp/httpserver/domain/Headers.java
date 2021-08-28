package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    private final Map<String, String> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public Headers(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public void addHeader(String headerName, String content) {
        headers.put(headerName, content);
    }

    public String contentLength() {
        if (!headers.containsKey("Content-Length")) {
            return "0";
        }
        return headers.get("Content-Length");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
