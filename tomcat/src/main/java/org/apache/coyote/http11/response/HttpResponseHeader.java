package org.apache.coyote.http11.response;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {
    private static final String ASSIGN_OPERATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        headers = new LinkedHashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void putCookie(String name, Map<String, String> cookies) {
        String value = cookies.keySet().stream()
                .map(cookie -> cookie + ASSIGN_OPERATOR + cookies.get(cookie))
                .collect(Collectors.joining(COOKIE_SEPARATOR));
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
