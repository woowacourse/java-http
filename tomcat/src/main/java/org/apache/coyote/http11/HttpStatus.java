package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200", "OK"),
    REDIRECT("302", "Moved Temporarily"),
    NOT_FOUND("404", "Not Found");

    private final String httpStatusCode;
    private final String httpStatusMessage;

    HttpStatus(String httpStatusCode, String httpStatusMessage) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMessage = httpStatusMessage;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getHttpStatusMessage() {
        return httpStatusMessage;
    }
}
