package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum Method {

    GET("GET"),
    POST("POST");

    final String name;

    Method(final String name) {
        this.name = name;
    }

    public static Method getMethod(final String input) {
        return Arrays.stream(Method.values())
                .filter(method -> method.name.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Method Request"));
    }
}
