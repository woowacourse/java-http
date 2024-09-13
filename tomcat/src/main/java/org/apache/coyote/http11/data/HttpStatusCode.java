package org.apache.coyote.http11.data;

public enum HttpStatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    FOUND(302, "Found");

    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getString() {
        return code + " " + message;
    }
}
