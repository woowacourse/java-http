package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "200 OK"),
    FOUND(302, "302 FOUND"),
    BAD_REQUEST(400, "400 BAD REQUEST"),
    UNAUTHORIZED(401, "401 UNAUTHORIZED"),
    NOT_FOUND(404, "404 NOT FOUND"),
    METHOD_NOT_ALLOWED(405, "405 METHOD NOT ALLOWED"),
    INTERNAL_SERVER_ERROR(500, "500 INTERNAL SERVER ERROR");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
