package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus findHttpStatusByUrl(final String url) {
        return Arrays.stream(values())
                .filter(httpStatus -> url.contains(httpStatus.code + ".html"))
                .findFirst()
                .orElseGet(() -> OK);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
