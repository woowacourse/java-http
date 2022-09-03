package org.apache.coyote.http11.http.response;

public enum HttpStatus {
    OK(200, "OK");

    private int code;
    private String message;

    HttpStatus(final int code, final String message) {
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
