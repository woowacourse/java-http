package org.apache.coyote.http11.httpmessage.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int value;
    private final String message;


    HttpStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
