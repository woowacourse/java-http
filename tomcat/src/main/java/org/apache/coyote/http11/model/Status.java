package org.apache.coyote.http11.model;

public enum Status {

    OK(200),
    ;

    private final int code;

    Status(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
