package org.apache.coyote.http11.httpmessage.response;

public enum StatusCode {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT_FOUND"),
    REDIRECT(302, "REDIRECT");

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
