package org.apache.coyote.global.exception;

public class URIUnmappedException extends RuntimeException {

    private static final String MESSAGE = "<%s> - Controller에서 매핑되지 않은 URI입니다.";

    public URIUnmappedException(String uri) {
        super(String.format(MESSAGE, uri));
    }
}
