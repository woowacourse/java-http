package nextstep.jwp.http.exception;

import java.util.Arrays;
import nextstep.jwp.http.header.element.HttpStatus;

public enum Exceptions {
    NOT_FOUND(HttpStatus.NOT_FOUND, NotFoundException.class),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, null),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, UnauthorizedException.class),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, BadRequestException.class);

    private final HttpStatus httpStatus;
    private final Class<?> typeToken;

    Exceptions(HttpStatus httpStatus, Class<?> typeToken) {
        this.httpStatus = httpStatus;
        this.typeToken = typeToken;
    }

    public static Exceptions findByException(Exception e) {
        return Arrays.stream(values())
            .filter(value -> e.getClass().equals(value.typeToken))
            .findAny()
            .orElse(INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return String.format("%d.html", httpStatus.getCode());
    }
}
