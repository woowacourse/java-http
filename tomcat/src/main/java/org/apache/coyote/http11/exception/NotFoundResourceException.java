package org.apache.coyote.http11.exception;

public class NotFoundResourceException extends RuntimeException {
    public NotFoundResourceException() {
        super("존재하지 않는 리소스입니다.");
    }
}
