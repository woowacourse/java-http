package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpStatusCode implements HttpComponent {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatusCode from(final String valueAndReasonPhrase) {
        return Arrays.stream(values())
                .filter(statusCode -> valueAndReasonPhrase.equals(statusCode.asString()))
                .findFirst()
                .orElseThrow(() -> new InvalidFormatException(valueAndReasonPhrase));
    }

    public int code() {
        return code;
    }

    @Override
    public String asString() {
        return code + " " + reasonPhrase;
    }
}
