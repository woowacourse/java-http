package org.apache.coyote.http11.exception;

public class RequestHeaderException extends RuntimeException {

    public RequestHeaderException() {
        super("헤더에 빈 문자열이 올 수 없습니다.");
    }
}
