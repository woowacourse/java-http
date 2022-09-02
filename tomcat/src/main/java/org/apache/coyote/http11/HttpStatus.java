package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found");

    private final int statusCode;
    private final String statusName;

    HttpStatus(final int statusCode, final String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public static HttpStatus valueOf(final int statusCode) {
        return Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.getStatusCode() == statusCode)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("그런 Http 상태 코드 없습니다. %d", statusCode)));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusName() {
        return statusName;
    }
}
