package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),;

    private final int code;
    private final String reasonPhrase;

    HttpStatusCode(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String getCodeString() {
        return String.valueOf(code);
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
