package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String statusText;

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
