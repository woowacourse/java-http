package org.apache.coyote.http.response;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    NOT_FOUND(404),
    ;

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public String getHttpStatusMessage() {
        return code + " " + name();
    }
}
