package org.apache.coyote.http11;

import java.util.Map;

public record Request(
        String method,
        String path,
        Map<String, String> params,
        Map<String, String> body
) {
}
