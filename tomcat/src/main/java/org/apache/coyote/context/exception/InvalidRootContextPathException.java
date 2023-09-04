package org.apache.coyote.context.exception;

public class InvalidRootContextPathException extends IllegalArgumentException {

    public InvalidRootContextPathException() {
        super("Root Context Path가 유효하지 않습니다.");
    }
}
