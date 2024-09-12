package org.apache.coyote.http11.request;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }
}
