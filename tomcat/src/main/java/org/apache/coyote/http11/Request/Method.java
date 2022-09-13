package org.apache.coyote.http11.Request;

import java.util.Arrays;

public enum Method {

    NOT_ALLOWED(""),
    POST("POST"),
    GET("GET");

    private final String method;

    Method(final String method) {
        this.method = method;
    }

    public static Method from(final String method) {
        if (method == null || method.isBlank()) {
            return NOT_ALLOWED;
        }

        return Arrays.stream(Method.values())
                .filter(it -> it.method.equals(method))
                .findFirst()
                .orElse(NOT_ALLOWED);
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isGet() {
        return this == GET;
    }
}
