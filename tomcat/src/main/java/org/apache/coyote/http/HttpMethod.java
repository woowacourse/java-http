package org.apache.coyote.http;

public enum HttpMethod {

    GET("get"),
    POST("post"),
    PUT("put"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    ;

    public final String value;

    HttpMethod(String value) {
        this.value = value;
    }
}
