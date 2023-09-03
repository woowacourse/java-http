package org.apache.coyote.http11.common;

public enum ResponseStatus {

    OK(200),
    FOUND(302);

    private final int statusCode;

    ResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
