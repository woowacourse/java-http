package org.apache.coyote.common.constant;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int number;
    private final String message;

    HttpStatus(final int number, final String message) {
        this.number = number;
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
