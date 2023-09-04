package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int statusCode;
    private final String statusText;

    HttpStatusCode(final int statusCode, final String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
