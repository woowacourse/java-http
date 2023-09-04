package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum HttpStatus {
    BAD_REQUEST(400, "400 BAD REQUEST"),
    CREATED(201, "201 CREATED"),
    FOUND(302, "302 FOUND"),
    OK(200, "200 OK"),
    UNAUTHORIZED(401, "401 UNAUTHORIZED"),
    NOT_FOUND(404, "404 NOT FOUND"),

    NOT(4011,"4011 NOT FOUND");
    private final int status;
    private final String statusName;

    HttpStatus(final int status, final String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static HttpStatus from(final String message) {
        return Arrays.stream(HttpStatus.values())
                .filter(status -> status.getStatusName().equalsIgnoreCase(message))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
    }

    public int getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }
}
