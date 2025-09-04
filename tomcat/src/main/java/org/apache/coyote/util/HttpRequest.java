package org.apache.coyote.util;

import java.util.Map;

public record HttpRequest(
        String method,
        String requestPath,
        Map<String, String> queryParameters,
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
                requestLine.path(),
                requestLine.queryParameters(),
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

    public String getParameterValue(String parameterKey) {
        if (queryParameters.get(parameterKey) == null) {
            throw new IllegalArgumentException("요청한 파라미터의 값이 존재하지 않습니다.");
        }
        return queryParameters.get(parameterKey);
    }
}
