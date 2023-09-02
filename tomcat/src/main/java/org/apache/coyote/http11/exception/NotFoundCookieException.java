package org.apache.coyote.http11.exception;

public class NotFoundCookieException extends RuntimeException {

    public NotFoundCookieException() {
        super("쿠키를 찾을 수 없습니다.");
    }
}
