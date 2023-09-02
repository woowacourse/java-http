package org.apache.coyote.http11;

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
