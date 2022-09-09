package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;

public enum ExceptionType {
    INTERNAL_SERVER_ERROR(List.of("Exception")),
    UNAUTHORIZED(List.of("NoSuchUserException")),
    BAD_REQUEST(List.of("IllegalArgumentException", "ExistUserException")),
    NOT_FOUND(List.of("NotFoundException"));

    private final List<String> exceptionClassName;

    ExceptionType(final List<String> exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public static ExceptionType from(String exceptionClassName) {
        return Arrays.stream(ExceptionType.values())
                .filter(exceptionType -> exceptionType.contains(exceptionClassName))
                .findAny()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    private boolean contains(String exceptionClassName) {
        return this.exceptionClassName.contains(exceptionClassName);
    }
}
