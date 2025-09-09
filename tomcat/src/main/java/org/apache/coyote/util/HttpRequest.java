package org.apache.coyote.util;

import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> requestHeaders,
        Cookies cookies,
        RequestBody requestBody
) {

    public static HttpRequest of(
            final RequestLine requestLine,
            final Map<String, String> requestHeaders,
            final Cookies cookies,
            final RequestBody requestBody
    ) {
        return new HttpRequest(
                requestLine,
                requestHeaders,
                cookies,
                requestBody
        );
    }

    public String getHttpVersion() {
        return requestLine.httpVersion();
    }

    public String getRequestPath() {
        return requestLine.path();
    }

    public String getRequestMethod() {
        return requestLine.method();
    }

    public String getHeaderValue(String header) {
        if (requestHeaders.get(header) == null) {
            throw new IllegalArgumentException("요청한 헤더가 존재하지 않습니다.");
        }
        return requestHeaders.get(header);
    }

    public String getParameterValue(String parameterKey) {
        if (requestLine.queryParameters().getValue(parameterKey) == null) {
            throw new IllegalArgumentException("요청한 파라미터의 값이 존재하지 않습니다.");
        }
        return requestLine.queryParameters().getValue(parameterKey);
    }

    public String extractMethodPath() {
        return requestLine.method() + requestLine.path();
    }

    public String getRequestValue(final String requestKey) {
        return requestBody.getValue(requestKey);
    }

    public Cookie getCookie(final String cookieName) {
        return cookies.getCookie(cookieName);
    }
}
