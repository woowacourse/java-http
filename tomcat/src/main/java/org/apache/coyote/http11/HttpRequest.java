package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> headers,
        byte[] body
) {
    public String bodyAsString() {
        return new String(body);
    }
}
