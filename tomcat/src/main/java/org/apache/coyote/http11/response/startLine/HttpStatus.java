package org.apache.coyote.http11.response.startLine;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String toMessage() {
        return getCode() + " " + getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
