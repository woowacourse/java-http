package org.apache.coyote.common.response;

public enum Status {

    OK("200 OK"),
    FOUND("302 FOUND"),
    UNAUTHORIZED("401 UNAUTHORIZED")
    ;

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
