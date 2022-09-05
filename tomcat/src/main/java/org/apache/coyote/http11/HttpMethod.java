package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod findByName(final String name) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바른 HTTP 요청 메서드 타입이 아닙니다."));
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
