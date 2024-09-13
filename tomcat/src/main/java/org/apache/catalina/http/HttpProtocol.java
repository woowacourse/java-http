package org.apache.catalina.http;

import java.util.Arrays;

public enum HttpProtocol {

    HTTP,
    HTTPS,
    ;

    public static HttpProtocol of(String value) {
        return Arrays.stream(values())
                .filter(httpProtocol -> httpProtocol.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(value + "는 존재하지 않는 HttpProtocol 입니다."));
    }
}
