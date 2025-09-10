package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200", "OK"),
    FOUND("302", "Found"),
    NOT_FOUND("404", "Not Found");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCodeWithMessage() {
        return code + " " + message + " ";
    }
}
