package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {
    private final Map<String, String> headers;

    public ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public ResponseHeaders() {
        this.headers = new HashMap<>();
    }

    public void add(final String key, final String value) {
        if (headers.putIfAbsent(key, value) != null) {
            final String originalValue = headers.get(key);
            headers.replace(key, originalValue + "; " + value);
        }
    }

    public void addAll(Map<String, String> headers) {
        headers.keySet().forEach(key -> add(key, headers.get(key)));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<String> getHeaderLines() {
        return headers.keySet().stream()
                .map(key -> key + ": " + headers.get(key) + " ")
                .collect(Collectors.toList());
    }
}
