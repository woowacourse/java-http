package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String statusLine() {
        return "HTTP/1.1 " + code + " " + reasonPhrase;
    }
}
