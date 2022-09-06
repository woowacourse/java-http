package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> httpRequestHeaders) {
        headers = new HashMap<>(httpRequestHeaders);
    }

    public void addAttribute(String key, String value) {
        headers.put(key, value);
    }

    public String toMessage() {
        return headers.entrySet().stream()
            .map(it -> it.getKey() + ": " + it.getValue())
            .collect(Collectors.joining(" \r\n"));
    }
}
