package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseLabel() {
        return statusCode + " " + this.name();
    }
}
