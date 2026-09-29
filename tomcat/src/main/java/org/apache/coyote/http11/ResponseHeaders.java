package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public List<String> toLines() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            lines.add(header.getKey() + ": " + header.getValue() + " ");
        }
        return lines;
    }
}
