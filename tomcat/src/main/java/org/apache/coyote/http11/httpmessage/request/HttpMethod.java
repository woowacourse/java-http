package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 Http 메서드 입니다."));
    }
}
