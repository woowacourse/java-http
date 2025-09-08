package org.apache.coyote.http11.vo;

import java.util.Map;

public record HttpRequest(
    String method,
    String uri,
    Map<String, String> headers
) {
}
