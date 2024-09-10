package org.apache.coyote.http11.domain;

public enum HttpStatusCode {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

    private final int code;

    HttpStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name();
    }
}
