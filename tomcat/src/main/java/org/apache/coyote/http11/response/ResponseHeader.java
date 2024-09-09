package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void add(final String name, final String value) {
        headers.put(name, value);
    }

    public String getHeaderResponse() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(" \r\n"));
    }
}
