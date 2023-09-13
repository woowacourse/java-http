package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpRequestMethod {

    POST,
    PATCH,
    PUT,
    GET,
    DELETE;

    public static HttpRequestMethod from(final String methodName) {
        return Arrays.stream(HttpRequestMethod.values())
                .filter(method -> methodName.equalsIgnoreCase(method.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 Http Method 입니다."));
    }
}
