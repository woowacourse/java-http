package org.apache.coyote.http11.constant;

public enum HttpStatus {
    OK(200, "OK"),
    REDIRECT(302, "Redirect"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int statusCode;
    private final String message;

    HttpStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
