package org.apache.http;

import java.util.Arrays;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(
            int code,
            String reasonPhrase
    ) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status ->
                        status.code == code
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 상태 코드: " + code)
                );
    }

}
