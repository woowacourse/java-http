package org.apache.coyote.util;

public record RequestLine(
        String method,
        String requestUrl,
        String httpVersion
) {

}
