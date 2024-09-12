package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int value;
    private final String reason;

    HttpStatus(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
