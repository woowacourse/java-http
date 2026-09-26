package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    NOT_FOUND("404", "Not Found");

    private final String code;

    private final String reasonPhrase;

    HttpStatus(String code, String reasonPhrase) {
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
