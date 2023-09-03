package org.apache.catalina.servlet.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    NOT_FOUND(404, "NOT FOUND"),
    ;

    private final int statusCode;
    private final String responsePhrase;

    HttpStatus(int statusCode, String responsePhrase) {
        this.statusCode = statusCode;
        this.responsePhrase = responsePhrase;
    }

    public int statusCode() {
        return statusCode;
    }

    public String responsePhrase() {
        return responsePhrase;
    }
}
