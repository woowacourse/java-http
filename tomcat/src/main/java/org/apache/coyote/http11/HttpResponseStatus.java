package org.apache.coyote.http11;

public enum HttpResponseStatus {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    NOT_FOUND(404, "NOT_FOUND");

    private final int code;
    private final String message;

    HttpResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code + " " + this.message;
    }
}
