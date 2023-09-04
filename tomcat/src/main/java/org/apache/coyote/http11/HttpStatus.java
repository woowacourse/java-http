package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND");

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
