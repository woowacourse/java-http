package org.apache.coyote.httprequest.exception;

public class InvalidHeaderException extends IllegalArgumentException {

    public InvalidHeaderException() {
        super("잘못된 요청 헤더 형식입니다.");
    }
}
