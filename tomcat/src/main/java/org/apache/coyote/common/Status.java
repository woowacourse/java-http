package org.apache.coyote.common;

public enum Status {

    OK("200 OK")
    ;

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
