package org.apache.catalina.request;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP,
    HTTPS,
    ;

    public static HttpProtocol of(String value) {
        return Arrays.stream(values())
                .filter(httpProtocol -> httpProtocol.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HttpProtocol 입니다."));
    }
}
