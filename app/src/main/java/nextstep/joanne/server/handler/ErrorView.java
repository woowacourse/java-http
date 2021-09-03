package nextstep.joanne.server.handler;

import nextstep.joanne.server.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum ErrorView {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "/400.html"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "/404.html"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "/401.html"),
    ;

    private final HttpStatus status;
    private final String view;

    ErrorView(HttpStatus status, String view) {
        this.status = status;
        this.view = view;
    }

    public static String viewOf(HttpStatus httpStatus) {
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.status, httpStatus))
                .findAny()
                .orElseGet(() -> INTERNAL_SERVER_ERROR)
                .view;
    }
}
