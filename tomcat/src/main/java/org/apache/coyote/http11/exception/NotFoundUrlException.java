package org.apache.coyote.http11.exception;

public class NotFoundUrlException extends RuntimeException {

    public NotFoundUrlException() {
        super("요청한 주소를 찾을 수 없습니다.");
    }
}
