package org.apache.coyote.http11.http.util;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String name;

    HttpMethod(final String name) {
        this.name = name;
    }

    public boolean isSameMethod(final String method) {
        return this.name.equals(method);
    }
}
