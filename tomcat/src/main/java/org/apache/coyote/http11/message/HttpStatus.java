package org.apache.coyote.http11.message;

public enum HttpStatus {

    OK("200", "OK"),
    FOUND("302", "FOUND");

    private final String statusCode;
    private final String reasonPhrase;

    HttpStatus(String statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public String toString() {
        return statusCode + " " + reasonPhrase;
    }
}
