package org.apache.coyote.http11.common;

public enum HttpStatus {

    OK(200, "OK"),

    FOUND(302, "Found"),

    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

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
