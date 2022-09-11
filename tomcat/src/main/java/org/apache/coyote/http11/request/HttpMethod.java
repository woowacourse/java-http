package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PUT, PATCH, DELETE, OPTIONS;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(method + "는 존재하지 않는 http method입니다."));
    }
}
