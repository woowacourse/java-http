package org.apache.coyote.model.request;

import org.apache.coyote.exception.NotFoundHttpMethodException;

import java.util.Arrays;
import java.util.Objects;

public enum Method {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    ;

    private final String method;

    Method(String method) {
        this.method = method;
    }

    public static Method of(String method) {
        Objects.requireNonNull(method);
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.method.equals(method))
                .findAny()
                .orElseThrow(() -> new NotFoundHttpMethodException("해당하는 HttpMethod를 찾을 수 없습니다."));
    }
}
