package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    DELETE,
    PATCH,
    PUT,
    ;

    public static HttpMethod from(String requestHttpMethod) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(requestHttpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드 요청입니다."));
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isDelete() {
        return this == DELETE;
    }

    public boolean isPatch() {
        return this == PATCH;
    }

    public boolean isPut() {
        return this == PUT;
    }
}
