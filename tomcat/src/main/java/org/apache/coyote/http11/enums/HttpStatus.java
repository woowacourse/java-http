package org.apache.coyote.http11.enums;

public enum HttpStatus {
    OK(200, "200 OK"),
    CREATED(201, "201 Created"),
    FOUND(302, "302 Found"),
    UNAUTHORIZED(401, "401 Unauthorized"),
    INTERNAL_SERVER_ERROR(500, "500 Internal Server Error");
    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
