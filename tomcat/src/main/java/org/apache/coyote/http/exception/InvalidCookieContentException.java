package org.apache.coyote.http.exception;

public class InvalidCookieContentException extends IllegalArgumentException {

    public InvalidCookieContentException() {
        super("허용되지 않는 쿠기 값입니다.");
    }
}
