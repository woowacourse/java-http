package org.apache.coyote.httprequest.exception;

public class InvalidRequestHeaderNameException extends IllegalArgumentException {

    public InvalidRequestHeaderNameException() {
        super("잘못된 요청 헤더 이름 입니다.");
    }
}
