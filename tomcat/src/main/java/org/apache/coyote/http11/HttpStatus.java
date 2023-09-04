package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302);

    public final int code;

    HttpStatus(int code) {
        this.code = code;
    }
}
