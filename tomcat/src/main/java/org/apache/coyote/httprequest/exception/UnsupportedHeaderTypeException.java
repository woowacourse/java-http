package org.apache.coyote.httprequest.exception;

public class UnsupportedHeaderTypeException extends IllegalArgumentException {

    public UnsupportedHeaderTypeException() {
        super("지원하지 않는 헤더 타입 입니다.");
    }
}
