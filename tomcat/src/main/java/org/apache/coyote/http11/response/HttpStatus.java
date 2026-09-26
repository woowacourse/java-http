package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    int code() {
        return code;
    }

    String reasonPhrase() {
        return reasonPhrase;
    }
}
