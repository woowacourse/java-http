package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;

public enum ExceptionType {
    INTERNAL_SERVER_ERROR("Exception"),
    UNAUTHORIZED("NoSuchUserException"),
    BAD_REQUEST("IllegalArgumentException", "ExistUserException"),
    NOT_FOUND("NotFoundException");

    private final List<String> exceptionClassName;

    ExceptionType(final String... exceptionClassName) {
        this.exceptionClassName = List.of(exceptionClassName);
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
