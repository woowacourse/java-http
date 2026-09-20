package org.apache.coyote.http11;

enum HttpStatus {

    OK(200, "OK"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(final int code, final String reasonPhrase) {
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
