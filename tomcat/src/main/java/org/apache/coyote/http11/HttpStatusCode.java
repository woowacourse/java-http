package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;

    HttpStatusCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String stringify() {
        return code + " " + message;
    }
}
