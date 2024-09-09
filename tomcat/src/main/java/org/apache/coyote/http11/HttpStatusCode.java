package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK("200 OK"),
    NOT_FOUND("404 Not Found"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized");

    private final String message;

    HttpStatusCode(String message) {
        this.message = message;
    }

    public String buildOutput() {
        return "HTTP/1.1 " + message + " ";
    }
}
