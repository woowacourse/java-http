package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(StatusCode.OK, "OK"),
    REDIRECT(StatusCode.REDIRECT, "FOUND");

    private final StatusCode statusCode;
    private final String name;

    HttpStatus(StatusCode statusCode, String name) {
        this.statusCode = statusCode;
        this.name = name;
    }

    public int getStatusCode() {
        return this.statusCode.value;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRedirect() {
        return this.statusCode.equals(StatusCode.REDIRECT);
    }

    private enum StatusCode {

        OK(200),
        REDIRECT(302);

        private final int value;

        StatusCode(int value) {
            this.value = value;
        }
    }
}
