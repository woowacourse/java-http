package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String status;

    StatusCode(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static String getStatusCode(int code) {
        return Arrays.stream(values())
                .filter(statusCode -> statusCode.code == code)
                .map(statusCode -> statusCode.code + " " + statusCode.status)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
