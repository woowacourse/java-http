package nextstep.jwp.http.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {
    private static final String COOKIE_KEY = "Cookie";
    private final Map<String, String> headers;

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String line) {
        String[] splits = line.split(": ", 2);
        addHeader(splits[0].trim(), splits[1].trim());
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

    public String getCookie() {
        return headers.get(COOKIE_KEY);
    }

    public List<String> asString() {
        List<String> list = new ArrayList<>();
        for (Entry<String, String> header : headers.entrySet()) {
            list.add(header.getKey() + ": " + header.getValue() + " ");
        }
        return list;
    }
}
