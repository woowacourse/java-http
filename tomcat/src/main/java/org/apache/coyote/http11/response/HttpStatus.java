package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String line() {
        return String.format("HTTP/1.1 %d %s", code, reasonPhrase);
    }
}
