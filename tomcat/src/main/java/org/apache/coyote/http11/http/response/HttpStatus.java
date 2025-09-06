package org.apache.coyote.http11.http.response;

public enum HttpStatus {

    OK(200),
    NOT_FOUND(404),
    ;

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public String getFormattedName() {
        return this.code + " " + this.name();
    }
}
