package org.apache.coyote.http11.common;

public enum Status {

    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
