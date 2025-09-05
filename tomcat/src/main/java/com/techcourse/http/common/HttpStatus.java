package com.techcourse.http.common;


public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatus(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String getStatusLine() {
        return String.format("%d %s", code, reasonPhrase);
    }
}
