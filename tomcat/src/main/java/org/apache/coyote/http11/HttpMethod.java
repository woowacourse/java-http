package org.apache.coyote.http11;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }

    public String getValue() {
        return value;
    }
}
