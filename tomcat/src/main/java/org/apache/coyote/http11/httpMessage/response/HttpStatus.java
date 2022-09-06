package org.apache.coyote.http11.httpmessage.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int value;
    private final String message;


    HttpStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public boolean isFound() {
        return this == FOUND;
    }

    public boolean isError() {
        return value >= 400;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
