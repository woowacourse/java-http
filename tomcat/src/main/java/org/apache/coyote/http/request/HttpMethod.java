package org.apache.coyote.http.request;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    ;

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public static boolean isHttpMethod(String method) {
        return Arrays.stream(HttpMethod.values()).anyMatch(httpMethod -> httpMethod.getMethodName().equals(method));
    }

    public static HttpMethod findMethodByMethodName(String name) {
        return Arrays.stream(HttpMethod.values()).filter(httpMethod -> httpMethod.getMethodName().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown http method: " + name));
    }
}
