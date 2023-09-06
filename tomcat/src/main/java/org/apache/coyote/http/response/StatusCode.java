package org.apache.coyote.http.response;

public enum StatusCode {

    OK(200),
    FOUND(302);

    private final int code;

    StatusCode(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
