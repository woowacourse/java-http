package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200");

    private final String statusCode;

    HttpStatus(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String toStatusFormat() {
        return this.statusCode + " " + this.name() + " ";
    }
}
