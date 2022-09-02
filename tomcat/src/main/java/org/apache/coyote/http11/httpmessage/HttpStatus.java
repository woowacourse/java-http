package org.apache.coyote.http11.httpmessage;

public enum HttpStatus {
    OK("200", "OK "),
    REDIRECT("302", "");

    private final String statusCode;
    private final String status;

    HttpStatus(String statusCode, String status) {
        this.statusCode = statusCode;
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }
}
