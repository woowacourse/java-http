package org.apache.coyote.http;

public enum StatusCode {

    OK("200 OK"),
    FOUND("302 Found");

    private final String statusCode;

    StatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
