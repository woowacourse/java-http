package org.apache.coyote.http11.exception;

public class NotFoundSessionException extends RuntimeException {

    public NotFoundSessionException() {
        super("세션이 존재하지 않습니다.");
    }
}
