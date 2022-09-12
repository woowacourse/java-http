package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 HttpMethod 입니다!"));
    }
}
