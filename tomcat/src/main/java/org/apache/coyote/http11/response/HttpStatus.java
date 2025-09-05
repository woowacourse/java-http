package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpStatus {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
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
