package org.apache.coyote.common.response;

public enum Status {

    OK("200 Ok"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized")
    ;

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
