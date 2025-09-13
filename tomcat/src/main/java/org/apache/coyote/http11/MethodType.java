package org.apache.coyote.http11;

import java.util.Arrays;

public enum MethodType {

    GET("GET"),
    POST("POST");

    private final String type;

    MethodType(String type) {
        this.type = type;
    }

    public static MethodType getMethodType(String method) {
        return Arrays.stream(MethodType.values())
                .filter(methodType -> methodType.type.equals(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] invalid method type"));
    }
}
