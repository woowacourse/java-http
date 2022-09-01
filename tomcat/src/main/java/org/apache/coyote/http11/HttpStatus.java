package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
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
}
