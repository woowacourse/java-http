package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public boolean has(HttpHeader httpHeader) {
        return headers.containsKey(httpHeader.getName());
    }

    public void put(HttpHeader httpHeader, String header) {
        headers.put(httpHeader.getName(), header);
    }

    public String get(HttpHeader httpHeader) {
        return headers.get(httpHeader.getName());
    }

    public String getMessage() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
