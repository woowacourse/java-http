package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(40, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int value;
    private final String reason;

    HttpStatus(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public String toHttpHeader() {
        return value + " " + reason;
    }

    public boolean isFound() {
        return this.equals(FOUND);
    }
}
