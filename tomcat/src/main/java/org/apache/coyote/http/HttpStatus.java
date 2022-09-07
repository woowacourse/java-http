package org.apache.coyote.http;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    CREATED(201, "CREATED");

    private Integer code;
    private String message;

    HttpStatus(final Integer code, final String message) {
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
