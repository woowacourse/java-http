package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK("200 OK"),
    NOT_FOUND("404 Not Found"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    BAD_REQUEST("400 Bad Request");

    private final String message;

    HttpStatusCode(String message) {
        this.message = message;
    }

    public String buildOutput() {
        return "HTTP/1.1 " + message + " ";
    }
}
