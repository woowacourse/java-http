package org.apache.coyote.http11.component;

import java.util.Map;
import java.util.Objects;

public enum Method {
    OPTION("OPTION"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private static final Map<String, Method> converter;

    static {
        converter = Map.of(
                OPTION.value, OPTION,
                GET.value, GET,
                HEAD.value, HEAD,
                POST.value, POST,
                PUT.value, PUT,
                DELETE.value, DELETE,
                TRACE.value, TRACE,
                CONNECT.value, CONNECT
        );
    }

    private final String value;

    Method(final String value) {
        this.value = value;
    }

    public static Method from(final String plaintext) {
        final var method = converter.get(plaintext);
        return Objects.requireNonNull(method);
    }

    public String getValue() {
        return value;
    }
}
