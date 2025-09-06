package com.techcourse.http.common;

import com.techcourse.exception.NotFoundException;
import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {

    GET("get"),
    POST("post"),
    PUT("put"),
    PATCH("patch"),
    DELETE("delete"),
    OPTIONS("options"),
    ;

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod from(final String method) {
        Objects.requireNonNull(method);

        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isEqualsMethod(method))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 http method 입니다."));
    }

    private boolean isEqualsMethod(final String method) {
        return this.method.equalsIgnoreCase(method);
    }
}
