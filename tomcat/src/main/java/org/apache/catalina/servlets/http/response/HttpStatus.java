package org.apache.catalina.servlets.http.response;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpStatus {

    OK(200),
    FOUND(302);

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static HttpStatus from(int statusCode) {
        return Arrays.stream(values())
                .filter(status -> status.getStatusCode() == statusCode)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
