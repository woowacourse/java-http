package com.techcourse.http.common;


public enum HttpStatus {

    OK(200, "OK"),

    NO_CONTENT(204, "No Content"),

    FOUND(302, "Found"),

    NOT_FOUND(404, "Not Found"),
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatus(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String toStatusLine() {
        return String.format("%d %s", code, reasonPhrase);
    }
}
