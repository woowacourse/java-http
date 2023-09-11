package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed");

    private final int code;
    private final String message;

    HttpStatusCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String stringify() {
        return code + " " + message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
