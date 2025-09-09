package org.apache.coyote;

public enum HttpStatus {

    OK("200 OK"),
    CREATED("201 Created"),
    FOUND("302 Found"),
    BAD_REQUEST("400 Bad Request"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found");

    private final String name;

    HttpStatus(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
