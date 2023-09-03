package org.apache.coyote.http11.request.requestLine;

import org.apache.coyote.http11.exception.IllegalHttpMethodException;

public enum HttpMethod {

    POST,
    GET,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    CONNECT,
    TRACE;

    public static HttpMethod from(final String httpMethod) {
        try {
            return HttpMethod.valueOf(httpMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalHttpMethodException("HTTP 메서드 정보가 올바르지 않습니다.");
        }
    }
}
