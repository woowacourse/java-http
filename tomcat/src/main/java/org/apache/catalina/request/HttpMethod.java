package org.apache.catalina.request;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
