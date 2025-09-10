package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, DELETE, PUT, PATCH, OPTION;

    public static HttpMethod of(String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("지원하지 않는 HTTP 메서드입니다: " + method));
    }
}
