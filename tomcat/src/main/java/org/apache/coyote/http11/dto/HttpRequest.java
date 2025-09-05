package org.apache.coyote.http11.dto;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> headers
) {
}
