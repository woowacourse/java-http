package org.apache.coyote.render;

public enum MethodType {
    GET("GET"),
    POST("POST");

    private String method;

    MethodType(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
