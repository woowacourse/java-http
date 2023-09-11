package org.apache.coyote.http11.exception;

public class UnsupportedRequestException extends RuntimeException {

    public UnsupportedRequestException() {
        super("지원하지 않는 요청입니다.");
    }
}
