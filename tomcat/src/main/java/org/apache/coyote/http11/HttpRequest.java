package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> headers,
        String path,
        Map<String, String> queries,
        byte[] body
) {
    public String bodyAsString() {
        return new String(body);
    }
}
