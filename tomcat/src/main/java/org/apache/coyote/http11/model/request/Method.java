package org.apache.coyote.http11.model.request;

import java.util.Arrays;

public enum Method {
    GET("GET"),
    POST("POST"),
    ;

    private final String name;

    Method(final String name) {
        this.name = name;
    }

    public static Method findMethod(String input) {
        return Arrays.stream(values())
                .filter(method -> method.name.equals(input))
                .findAny()
                .orElseThrow(() ->  new IllegalArgumentException("Request Method가 올바르지 않습니다."));
    }

    public String getName() {
        return name;
    }
}
