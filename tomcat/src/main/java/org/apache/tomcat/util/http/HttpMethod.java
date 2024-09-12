package org.apache.tomcat.util.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    HttpMethod() {
    }

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("요청하신 HTTP METHOD는 지원하지 않습니다."));
    }
}
