package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int statusCode;
    private final String message;

    HttpStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getStatus() {
        return statusCode + " " + message;
    }
}
