package org.apache.coyote.http11.response;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    ;

    final int code;
    final String message;

    StatusCode(final int code,
               final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
