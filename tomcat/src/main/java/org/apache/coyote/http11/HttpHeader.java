package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String headerName, String value) {
        headers.put(headerName, value);
    }

    public String toHttpHeader() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ")
                    .append("\r\n");
        }

        return stringBuilder.toString();
    }
}
