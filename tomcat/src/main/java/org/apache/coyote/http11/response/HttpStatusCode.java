package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum HttpStatusCode {
    OK(200, "Ok"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int code;
    private final String reasonPhrase;

    HttpStatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatusCode valueOf(int code) {
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HttpStatusCode code: " + code));
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name();
    }
}
