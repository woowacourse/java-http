package org.apache.coyote.http11.common;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR")
    ;

    private final Integer value;
    private final String reasonPhrase;

    HttpStatus(final Integer value, final String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public Integer getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
