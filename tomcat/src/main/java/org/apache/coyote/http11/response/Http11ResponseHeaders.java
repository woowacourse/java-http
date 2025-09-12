package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

class Http11ResponseHeaders {
    private static final String CRLF = "\r\n";

    private final Map<String, String> headers = new LinkedHashMap<>();

    private Http11ResponseHeaders() {
    }

    static Http11ResponseHeaders create() {
        return new Http11ResponseHeaders();
    }

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
