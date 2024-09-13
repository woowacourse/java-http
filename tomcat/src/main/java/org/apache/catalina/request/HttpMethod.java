package org.apache.catalina.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    DELETE,
    PATCH,
    PUT,
    ;

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(value + "는 존재하지 않는 HttpMethod 입니다."));
    }
}
