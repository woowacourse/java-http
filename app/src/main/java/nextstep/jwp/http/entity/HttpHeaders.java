package nextstep.jwp.http.entity;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String line) {
        String[] splits = line.split(": ", 2);
        addHeader(splits[0], splits[1]);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public boolean hasHeaderName(String headerName) {
        return headers.containsKey(headerName);
    }

    public String get(String headerName) {
        return headers.get(headerName);
    }
}
