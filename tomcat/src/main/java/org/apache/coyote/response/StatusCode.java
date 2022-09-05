package org.apache.coyote.response;

public enum StatusCode {
    OK("200 OK");

    private final String statusCode;

    StatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return this.statusCode;
    }
}
