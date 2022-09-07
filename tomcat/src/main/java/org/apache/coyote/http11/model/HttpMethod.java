package org.apache.coyote.http11.model;

import java.util.stream.Stream;

import org.apache.coyote.exception.InvalidHttpMethodException;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod of(String value) {
        return Stream.of(HttpMethod.values())
                .filter(v -> v.value.equals(value))
                .findAny()
                .orElseThrow(InvalidHttpMethodException::new);
    }

    public String getValue() {
        return value;
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
