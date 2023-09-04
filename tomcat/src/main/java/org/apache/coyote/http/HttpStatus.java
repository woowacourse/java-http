package org.apache.coyote.http;

public enum HttpStatus {

    OK("200 OK"),
    REDIRECT("302 Redirect"),
    NOT_FOUND("404 Not Found"),
    BAD_REQUEST("400 Bad Request"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String status;

    HttpStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
