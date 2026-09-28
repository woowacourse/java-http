package org.apache.coyote.http11.request;

import java.util.Map;

public record ParsedTarget(
        String path,
        Map<String, String> queryParameters
) {
}
