package org.apache.coyote.common;

public enum StatusCode {

    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    ;

    private final String code;
    private final String reasonPhrase;

    StatusCode(final String code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
