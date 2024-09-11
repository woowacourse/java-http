package org.apache.coyote.http11.request;

public enum Method {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    Method(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
