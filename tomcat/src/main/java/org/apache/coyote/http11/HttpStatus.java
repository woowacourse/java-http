package org.apache.coyote.http11;

public enum HttpStatus {
    NOT_FOUND(404),
    FORBIDDEN(403),
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    METHOD_NOT_ALLOWED(405),
    NOT_MODIFIED(304),
    FOUND(302),
    ;

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public String toString() {
        return code + " " + name();
    }
}
