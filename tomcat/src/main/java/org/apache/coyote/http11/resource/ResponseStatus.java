package org.apache.coyote.http11.resource;

public enum ResponseStatus {
    OK("200"),
    REDIRECT("302"),
    UNAUTHORIZED("401");

    private final String code;

    ResponseStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
