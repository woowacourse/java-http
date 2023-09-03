package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum Method {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String methodName;

    Method(String methodName) {
        this.methodName = methodName;
    }

    public static Method of(String targetMethod) {
        if (targetMethod == null) {
            throw new IllegalArgumentException("null 에 대한 메서드는 존재하지 않습니다.");
        }
        return Arrays.stream(Method.values())
            .filter(method -> targetMethod.toUpperCase().equals(method.methodName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 메서드 입니다."));
    }
}
