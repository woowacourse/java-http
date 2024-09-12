package org.apache.coyote.http11.component;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion parse(String name) {
        return Arrays.stream(values())
                .filter(version -> version.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않는 HTTP 버전입니다."));
    }

    public String getName() {
        return name;
    }
}
