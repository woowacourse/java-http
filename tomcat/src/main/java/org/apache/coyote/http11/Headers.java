package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(String headerLine) {
        parseHeader(headerLine);
    }

    private void parseHeader(String headerLine) {
        String[] parts = headerLine.split(": ", 2);
        if (parts.length == 2) {
            headers.put(parts[0], parts[1]);
        }
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }
}
