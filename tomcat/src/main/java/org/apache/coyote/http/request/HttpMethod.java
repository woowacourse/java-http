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

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static boolean isHttpMethod(String method) {
        return Arrays.stream(HttpMethod.values()).anyMatch(httpMethod -> httpMethod.getMethod().equals(method));
    }

    public static HttpMethod findMethodByMethodName(String name) {
        return Arrays.stream(HttpMethod.values()).filter(httpMethod -> httpMethod.getMethod().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown http method: " + name));
    }
}
