package org.apache.coyote.http11.common;

public enum HttpStatus {

    OK(200, "OK")
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
