package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getStatusCodeAndMessage(int code) {
        return Arrays.stream(values())
                .filter(statusCode -> statusCode.code == code)
                .map(statusCode -> statusCode.code + " " + statusCode.message)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
