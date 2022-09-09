package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {
    private final Map<String, String> headers;

    private HttpHeader(Map<String, String> httpRequestHeaders) {
        headers = new HashMap<>(httpRequestHeaders);
    }

    public static HttpHeader from(Map<String, String> httpRequestHeaders) {
        return new HttpHeader(httpRequestHeaders);
    }

    public void addAttribute(String key, String value) {
        headers.put(key, value);
    }

    public String getAttributeOrDefault(String key, String defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    public String toMessage() {
        return headers.entrySet().stream()
            .map(it -> it.getKey() + ": " + it.getValue())
            .collect(Collectors.joining(" \r\n"));
    }
}
