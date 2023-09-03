package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "Ok"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
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
