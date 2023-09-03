package org.apache.coyote.http11.exception;

public class NotFoundAccountException extends RuntimeException {
    public NotFoundAccountException() {
        super("사용자 정보가 일치하지 않습니다.");
    }
}
