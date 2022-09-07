package org.apache.coyote.http11.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        if (!checkDuplicateKey(key)) {
            headers.put(key, value);
        }
    }

    public void addHeaders(HttpHeaders headers) {
        if (headers != null) {
            Map<String, String> headersList = headers.getHeaders();
            this.headers.putAll(headersList);
        }
    }

    private boolean checkDuplicateKey(String key) {
        return headers.containsKey(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String get(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        }
        throw new IllegalArgumentException();
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String toMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : headers.keySet()) {
            stringBuilder.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append(" ")
                    .append("\r\n");
        }
        return stringBuilder.toString();
    }
}
