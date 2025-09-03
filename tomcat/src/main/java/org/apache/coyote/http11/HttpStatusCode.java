package org.apache.coyote.http11;

public enum HttpStatusCode {
    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "ACCEPTED"),
    NO_CONTENT(204, "NO_CONTENT"),

    // 4xx Client Error
    BAD_REQUEST(400, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND");

    final int code;
    final String message;

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
