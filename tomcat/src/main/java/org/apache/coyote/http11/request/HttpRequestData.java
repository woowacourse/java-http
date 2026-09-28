package org.apache.coyote.http11.request;

import java.util.Map;

public record HttpRequestData(
        RequestLine requestLine,
        Map<String, String>headers,
        Map<String, String> body
) {
}
