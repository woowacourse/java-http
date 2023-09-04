package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod from(String methodName) {
        return Arrays.stream(HttpMethod.values())
                .filter(it -> it.methodName.equals(methodName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메서드입니다"));
    }

    public String methodName() {
        return methodName;
    }
}
