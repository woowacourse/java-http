package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    public static HttpMethod find(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP METHOD 입니다."));
    }

    public String getName() {
        return name();
    }
}
