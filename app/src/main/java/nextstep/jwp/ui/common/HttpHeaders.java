package nextstep.jwp.ui.common;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {
    private Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void put(String name, String value) {
        headers.put(name, value);
    }

    public String convertToLines() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
