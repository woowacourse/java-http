package com.techcourse;

public enum HttpStaus {
    OK("200 OK"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    BAD_REQUEST("400 Bad Request"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized"),
    NOT_FOUND("404 Not Found");

    private final String value;

    HttpStaus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}