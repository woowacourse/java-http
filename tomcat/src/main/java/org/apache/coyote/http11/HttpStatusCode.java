package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int statusCode;
    private final String reasonPhrase;

    HttpStatusCode(final int statusCode, final String reasonPhrase) {
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
