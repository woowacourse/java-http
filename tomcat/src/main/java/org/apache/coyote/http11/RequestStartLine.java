package org.apache.coyote.http11;

public record RequestStartLine(
        RequestMethod requestMethod,
        String requestUrl,
        String httpVersion
) {
}
