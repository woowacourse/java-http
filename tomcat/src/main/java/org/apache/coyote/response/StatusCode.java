package org.apache.coyote.response;

import java.util.Arrays;

public enum StatusCode {
    OK("200 OK"),
    FOUND("302 Found"),
    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String statusCode;

    StatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public static StatusCode findSameStatusCode(String statusName) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(statusName))
                .findAny()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return this.statusCode;
    }
}
