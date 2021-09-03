package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public String responseFormat() {
        return headers.keySet().stream()
                      .map(key -> key + ": " + headers.get(key) + " ")
                      .collect(Collectors.joining("\r\n"));
    }

    public String getCookie() {
        if (!headers.containsKey("Cookie")) {
            return "";
        }
        return headers.get("Cookie");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
