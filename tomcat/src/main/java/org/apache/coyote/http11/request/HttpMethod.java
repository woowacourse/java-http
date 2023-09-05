package org.apache.coyote.http11.request;

import java.util.Arrays;

import org.apache.coyote.http11.exception.RequestException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTION,
    CONNECT,
    TRACE;

    public static HttpMethod findByName(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findAny()
                .orElseThrow(() -> new RequestException("올바르지 않은 요청메서드 입니다."));
    }
}
