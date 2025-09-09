package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, List<String>> headers,
        String path,
        Map<String, String> queries,
        byte[] body
) {
}
