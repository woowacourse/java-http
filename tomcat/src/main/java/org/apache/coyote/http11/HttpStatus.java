package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 FOUND");

    private String statusCode;

    HttpStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
