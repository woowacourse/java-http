package org.apache.coyote.context.exception;

public class InvalidStaticResourcePathException extends IllegalArgumentException {

    public InvalidStaticResourcePathException() {
        super("Static Resource Path가 유효하지 않습니다.");
    }
}
