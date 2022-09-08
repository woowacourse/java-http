package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.catalina.exception.InvalidHttpMethodException;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    private final String methodName;

    HttpMethod(final String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod of(final String methodName) {
        return Arrays.stream(values())
                .filter(method -> method.methodName.equals(methodName))
                .findFirst()
                .orElseThrow(InvalidHttpMethodException::new);
    }

    public String getMethodName() {
        return methodName;
    }
}
