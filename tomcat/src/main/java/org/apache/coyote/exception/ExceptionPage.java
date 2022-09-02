package org.apache.coyote.exception;

import java.util.Arrays;
import org.apache.coyote.support.HttpStatus;

public enum ExceptionPage {

    NOT_FOUND(HttpStatus.NOT_FOUND, "/404.html"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html")
    ;

    private final HttpStatus status;
    private final String uri;

    ExceptionPage(HttpStatus status, String uri) {
        this.status = status;
        this.uri = uri;
    }

    public static String toUri(HttpStatus status) {
        return Arrays.stream(values())
                .filter(it -> it.status.equals(status))
                .map(it -> it.uri)
                .findAny()
                .orElse(INTERNAL_SERVER_ERROR.uri);
    }
}
