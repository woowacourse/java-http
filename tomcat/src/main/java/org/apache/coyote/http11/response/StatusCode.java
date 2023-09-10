package org.apache.coyote.http11.response;

public enum StatusCode {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
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
