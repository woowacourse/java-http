package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequestHeader(
        RequestLine firstLine,
        Map<String, String> header
) {
}
