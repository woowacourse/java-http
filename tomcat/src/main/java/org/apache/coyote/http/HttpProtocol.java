package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP1("HTTP/1.1");

    private final String name;

    HttpProtocol(String name) {
        this.name = name;
    }

    public static HttpProtocol from(String protocol) {
        return Arrays.stream(HttpProtocol.values())
            .filter(httpProtocol -> httpProtocol.getName().equals(protocol))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Protocol 입니다."));
    }

    public String getName() {
        return name;
    }
}
