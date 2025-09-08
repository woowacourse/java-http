package org.apache.coyote;

import java.util.Map;

public record HttpRequest(
        String method,
        String uri,
        Map<String, String> headers
) {
}
