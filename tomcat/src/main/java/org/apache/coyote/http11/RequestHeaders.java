package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    private Map<String, String> parseHeaders(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();

        for (String line : headerLines) {
            String[] keyValue = line.split(": ", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0], keyValue[1]);
            }
        }
        return headers;
    }

    public String get(String name) {
        return headers.get(name);
    }
}
