package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String reasonPhrase;

    private HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public String toString() {
        return code + " " + reasonPhrase;
    }
}
