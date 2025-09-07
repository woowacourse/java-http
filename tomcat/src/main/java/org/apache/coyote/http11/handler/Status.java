package org.apache.coyote.http11.handler;

public enum Status {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private final String reason;

    Status(final int code, final String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String line() {
        return String.format("%d %s", code, reason);
    }
}
