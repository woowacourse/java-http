package org.apache.http;

public enum HttpStatus {

    OK(200), BAD_REQUEST(400), INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
