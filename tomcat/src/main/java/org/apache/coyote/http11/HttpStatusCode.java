package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200),
    FOUND(302);

    private final int statusCode;

    HttpStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String toResponseFormat() {
        return statusCode + " " + name();
    }
}
