package org.apache.coyote.common;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String name) {
        this.code = code;
        this.message = name;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
