package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod of(String targetMethod) {
        if (targetMethod == null) {
            throw new IllegalArgumentException("null 에 대한 메서드는 존재하지 않습니다.");
        }
        return Arrays.stream(HttpMethod.values())
            .filter(httpMethod -> targetMethod.toUpperCase().equals(httpMethod.name()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 메서드 입니다."));
    }
}
