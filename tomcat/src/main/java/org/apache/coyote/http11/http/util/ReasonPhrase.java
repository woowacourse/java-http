package org.apache.coyote.http11.http.util;

public enum ReasonPhrase {

    OK("OK", 200),
    FOUND("Found", 302),
    ;

    private final String value;
    private final int statusCode;

    ReasonPhrase(final String value, final int statusCode) {
        this.value = value;
        this.statusCode = statusCode;
    }

    public String getValue() {
        return value;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
