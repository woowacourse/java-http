package org.apache.catalina.container.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    public boolean containKey(String key) {
        return headers.containsKey(key);
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    private Map<String, String> parseHeaders(List<String> headerLines) {
        Map<String, String> headerRead = new HashMap<>();
        for (String headerLine : headerLines) {
            int separatorIndex = headerLine.indexOf(":");
            String key = headerLine.substring(0, separatorIndex).trim().toLowerCase();
            String value = headerLine.substring(separatorIndex + 1).trim();
            headerRead.put(key, value);
        }
        return headerRead;
    }
}
