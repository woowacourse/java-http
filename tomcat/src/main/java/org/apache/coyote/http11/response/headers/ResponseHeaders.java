package org.apache.coyote.http11.response.headers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public List<String> toLines() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();
    }

}
