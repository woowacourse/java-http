package org.apache.coyote.http11.response;

public enum HttpResponseStatus {
    OK(200),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int statusCode;

    HttpResponseStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
