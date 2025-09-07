package org.apache.coyote.http11;

import java.util.Map;

record RequestLine(
        String method,
        String path,
        Map<String, String> queryParams
) {
}
