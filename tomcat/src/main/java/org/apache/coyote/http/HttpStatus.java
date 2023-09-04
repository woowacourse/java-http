package org.apache.coyote.http;

public enum HttpStatus {

    OK("200 OK"),
    REDIRECT("302 Redirect"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String status;

    HttpStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
