package nextstep.jwp.ui.response;

import nextstep.jwp.exception.NotMatchHttpStatusException;

import java.util.Arrays;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int code;
    private final String name;

    HttpStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String convert(HttpStatus httpStatus) {
        return Arrays.stream(values())
                .filter(status -> status.code == httpStatus.code)
                .map(status -> status.code + " " + status.name)
                .findFirst()
                .orElseThrow(NotMatchHttpStatusException::new);
    }

    public static String getPath(HttpStatus status) {
        return "/" + status.code + ".html";
    }
}
