package org.apache.coyote.http11.httpmessage.response;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allow");

    private final int value;
    private final String message;

    StatusCode(final int value, final String message) {
        this.value = value;
        this.message = message;
    }

    public String getStatus() {
        return value + " " + message;
    }
}
