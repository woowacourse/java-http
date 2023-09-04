package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String name;

    HttpStatusCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
