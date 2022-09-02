package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    ;

    private final int statusCode;
    private final String reasonPhrase;

    HttpStatus(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
