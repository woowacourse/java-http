package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HttpMethod {
    GET("get"),
    POST("post");

    private static final String NO_EXIST_HTTP_METHOD = "존재하지 않는 http 메서드입니다.";

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpMethod toHttpMethod(String methodName) {
        return Arrays.stream(values())
                .filter(element -> element.name.equalsIgnoreCase(methodName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NO_EXIST_HTTP_METHOD));
    }
}
