package org.apache.coyote.http11;

public enum HttpStatus {
    OK("OK", 200),
    CREATED("Created", 201),
    FOUND("Found", 302),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500);

    private final String status;
    private final int statusCode;

    HttpStatus(String status, int statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
