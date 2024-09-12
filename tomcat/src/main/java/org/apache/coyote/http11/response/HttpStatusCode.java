package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED");

    private final int code;

    private final String message;

    HttpStatusCode(int code, String message) {
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
