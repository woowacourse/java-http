package org.apache.coyote.http11.component.common;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Method {
    OPTION("OPTION"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private static final Map<String, Method> CONVERTER = Arrays.stream(Method.values())
            .collect(Collectors.toMap(Method::getValue, Function.identity()));

    private final String value;

    Method(final String value) {
        this.value = value;
    }

    public static Method from(final String plaintext) {
        final var method = CONVERTER.get(plaintext);
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException("존재하지 않는 Http Method");
        }
        return method;
    }

    public String getValue() {
        return value;
    }
}
