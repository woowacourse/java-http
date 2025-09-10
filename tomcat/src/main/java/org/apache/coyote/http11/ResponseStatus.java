package org.apache.coyote.http11;

public enum ResponseStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SEVER ERROR")
    ;

    private final int statusCode;
    private final String statusMessage;

    ResponseStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getResponseHeader() {
        return statusCode + " " + statusMessage;
    }
}
