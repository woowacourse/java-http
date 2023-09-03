package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    BAD_REQUEST(401, "BAD_REQUEST");

    private int statusCode;
    private String statusText;

    HttpStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusText() {
        return statusText;
    }
}
