package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class Http11ResponseHeaders {
    private static final String CRLF = "\r\n";

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public String toHeaderString() {
        return headers.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue() + " ")
                .reduce((h1, h2) -> h1 + CRLF + h2)
                .orElse("");
    }
}
