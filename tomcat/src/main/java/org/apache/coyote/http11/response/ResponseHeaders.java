package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void put(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            sb.append(key).append(": ").append(headers.get(key)).append(" \r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
