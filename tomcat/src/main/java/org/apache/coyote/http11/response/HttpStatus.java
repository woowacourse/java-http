package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatusCodeAndMessage() {
        return this.code + " " + this.message;
    }
}
