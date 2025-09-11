package org.apache.coyote.http11.model;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }
}
