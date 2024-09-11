package org.apache.coyote.http11;

public enum Status {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String message;

    Status(int code, String message) {
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
