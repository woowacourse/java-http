package org.apache.coyote.http11.response;

public enum ResponseStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    ;

    private static final String FORMAT_OF_STATUS = "%d %s";

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String buildHttpMessage() {
        return String.format(FORMAT_OF_STATUS, code, message);
    }
}
