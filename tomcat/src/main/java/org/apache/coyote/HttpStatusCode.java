package org.apache.coyote;

public enum HttpStatusCode {

    SUCCESS("200 OK"),
    FOUND("302 Found");

    private final String value;

    HttpStatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
