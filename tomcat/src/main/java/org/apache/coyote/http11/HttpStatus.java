package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return code + " " + reasonPhrase;
    }
}
