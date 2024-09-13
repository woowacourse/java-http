package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {
    private final Map<String, String> headers;

    public HttpResponseHeader() {
        headers = new LinkedHashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public String toString() {
        return headers.entrySet().stream()
                .map(header -> header.getKey() + ": " + header.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void putCookie(String name, Map<String, String> cookies) {
        String value = cookies.keySet().stream()
                .map(cookie -> cookie + "=" + cookies.get(cookie))
                .collect(Collectors.joining("; "));
        headers.put(name, value);
    }
}
