package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK("200 OK");

    private final String value;

    HttpStatusCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
