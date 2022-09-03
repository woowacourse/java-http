package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found")
    ;

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String toResponseMessage() {
        return code + " " + message;
    }

    public boolean isNotFound() {
        return this.equals(NOT_FOUND);
    }
}
