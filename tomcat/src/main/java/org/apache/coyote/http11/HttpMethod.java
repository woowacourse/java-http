package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod findHttpMethod(String methodName) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("메소드를 확인해주세요."));
    }
}
