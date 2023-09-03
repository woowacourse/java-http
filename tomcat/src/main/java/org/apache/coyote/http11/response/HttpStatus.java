package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
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
