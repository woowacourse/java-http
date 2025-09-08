package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public record ResponseHeaders(LinkedHashMap<String, String> headers) {

    public ResponseHeaders() {
        this(new LinkedHashMap<>());
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public byte[] getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        stringBuilder.append("\r\n");
        return stringBuilder.toString().getBytes();
    }
}
