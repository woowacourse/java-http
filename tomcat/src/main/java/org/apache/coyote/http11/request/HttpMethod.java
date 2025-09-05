package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    ;

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "http method " + name + " doesn't exist")
                );
    }

    public String getName() {
        return name;
    }
}
