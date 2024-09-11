package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpStatusCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    ;

    private final int value;
    private final String reasonPhrase;

    HttpStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatusCode from(final String valueAndReasonPhrase) {
        return Arrays.stream(values())
                .filter(statusCode -> valueAndReasonPhrase.equals(statusCode.asString()))
                .findFirst()
                .orElseThrow(() -> new InvalidFormatException(valueAndReasonPhrase));
    }

    public int value() {
        return value;
    }

    public String asString() {
        return value + " " + reasonPhrase;
    }
}
