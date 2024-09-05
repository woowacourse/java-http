package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST")
    ;

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod getHttpMethod(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name.equals(name))
                .findAny()
                .orElseThrow();
    }

    public boolean isMethod(String name) {
        return this.name.equals(name);
    }
}
