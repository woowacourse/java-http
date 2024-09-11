package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum Method {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    Method(String value) {
        this.value = value;
    }

    public static Method from(String value) {
        return Arrays.stream(Method.values())
                .filter(method -> method.value.equals(value))
                .findAny()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }
}
