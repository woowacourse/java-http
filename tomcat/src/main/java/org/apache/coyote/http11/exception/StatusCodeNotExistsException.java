package org.apache.coyote.http11.exception;

public class StatusCodeNotExistsException extends RuntimeException {
    public StatusCodeNotExistsException(int code) {
        super("존재하지 않는 상태코드: " + code);
    }
}
