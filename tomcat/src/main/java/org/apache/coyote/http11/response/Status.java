package org.apache.coyote.http11.response;

public enum Status {

    OK(200, "OK");

    private final int code;

    private final String message;

    Status(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatusForResponse() {
        return code + " " + message + " ";
    }
}
