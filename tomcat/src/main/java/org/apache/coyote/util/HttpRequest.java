package org.apache.coyote.util;

import java.util.Map;

public record HttpRequest(
        String method,
        String requestUrl,
        String httpVersion,
        Map<String, String> requestHeaders,
        String requestBody
) {

    public static HttpRequest of(
            final RequestLine requestLine,
            final Map<String, String> requestHeaders,
            final String requestBody
    ) {
        return new HttpRequest(
                requestLine.method(),
                requestLine.requestUrl(),
                requestLine.httpVersion(),
                requestHeaders,
                requestBody
        );
    }

    public String getHeaderValue(String header) {
        if (requestHeaders.get(header) == null) {
            throw new IllegalArgumentException("요청한 헤더가 존재하지 않습니다.");
        }
        return requestHeaders.get(header);
    }
}
