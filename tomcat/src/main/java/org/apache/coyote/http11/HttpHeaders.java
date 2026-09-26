package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void add(String line) {
        int colonIndex = line.indexOf(":");

        if (colonIndex == -1) {
            return;
        }

        String name = line.substring(0, colonIndex)
                .trim()
                .toLowerCase();
        String value = line.substring(colonIndex + 1).trim();

        headers.put(name, value);
    }

    public String get(String name) {
        return headers.get(name.toLowerCase());
    }

    public int contentLength() {
        return Integer.parseInt(headers.getOrDefault("content-length", "0"));
    }
}
