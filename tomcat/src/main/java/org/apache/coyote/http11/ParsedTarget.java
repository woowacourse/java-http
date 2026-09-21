package org.apache.coyote.http11;

import java.util.Map;

public record ParsedTarget(
        String path,
        Map<String, String> queryParameters
) {
}
