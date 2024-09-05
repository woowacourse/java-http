package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK("200 OK"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized"),
    ;

    private final String value;

    HttpStatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
