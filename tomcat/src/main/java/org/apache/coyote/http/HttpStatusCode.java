package org.apache.coyote.http;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    ;

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
