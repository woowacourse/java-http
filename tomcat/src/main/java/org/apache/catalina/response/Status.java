package org.apache.catalina.response;

public enum Status {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    ;

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
