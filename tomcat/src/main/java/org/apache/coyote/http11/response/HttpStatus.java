package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int httpStatusCode;
    private final String reasonPhrase;

    HttpStatus(int httpStatusCode, String reasonPhrase) {
        this.httpStatusCode = httpStatusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus from(int httpStatusCode) {
        return Arrays.stream(values())
                .filter(httpStatus -> httpStatus.httpStatusCode == httpStatusCode)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "http status code " + httpStatusCode + " doesn't exist")
                );
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
