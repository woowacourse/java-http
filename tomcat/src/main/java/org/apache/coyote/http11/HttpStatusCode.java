package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatusCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String toHttpString() {
        return code + " " + message;
    }

    public int getCode() {
        return code;
    }
}
