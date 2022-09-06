package org.apache.coyote.http11.http.domain;

public enum StatusCode {

    OK("200", "OK"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    ;

    private final String statusCode;
    private final String reasonPhrase;

    StatusCode(final String statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getStatusCode() {
        return statusCode + " " + reasonPhrase;
    }
}
