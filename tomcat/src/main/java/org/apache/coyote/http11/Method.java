package org.apache.coyote.http11;

public enum Method {

    GET("GET"),
    POST("POST"),
    ;

    private final String name;

    Method(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean matches(String method) {
        return name.equalsIgnoreCase(method);
    }
}
