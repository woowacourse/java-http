package org.apache.coyote.http11.httpmessage.request.requestline;

import java.util.Arrays;

public enum Method {
    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private final String name;

    Method(final String name) {
        this.name = name;
    }

    public static Method find(final String method) {
        return Arrays.stream(values()).filter(it -> it.isMatchName(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP Method 입니다."));
    }

    public boolean isMatchName(final String name) {
        return this.name.equals(name);
    }

    public boolean isGet() {
        return this == GET;
    }
}
