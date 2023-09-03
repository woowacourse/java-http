package org.apache.coyote.http11.response.header;

public enum Status {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    FOUND(302, "FOUND");

    private static final String VERSION = "HTTP/1.1";

    private final int code;
    private final String message;

    Status(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return VERSION + " " + this.code + " " + this.message + " ";
    }
}
