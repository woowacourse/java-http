package org.apache.coyote.http;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String message;

    HttpStatusCode(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getResponseStartLine() {
        return "HTTP/1.1 " + statusCode + " " + message;
    }
}
