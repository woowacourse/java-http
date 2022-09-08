package org.apache.coyote.http11.response.header;

public enum HttpStatusCode {

    OK("200 OK "),
    FOUND("302 Found "),
    NOT_FOUND("404 Not Found ");

    private final String value;

    HttpStatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFound() {
        return this == FOUND;
    }
}
