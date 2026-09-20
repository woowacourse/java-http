package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "200 OK"),
    FOUND(302, "302 Found"),
    UNAUTHORIZED(401, "401 Unauthorized");

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
