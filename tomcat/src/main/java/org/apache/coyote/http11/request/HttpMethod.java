package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    private boolean isSameMethod(String method) {
        return name().equals(method);
    }

    public static HttpMethod getByName(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isSameMethod(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다."));
    }
}
