package org.apache.coyote.httprequest.exception;

public class InvalidConnectionHeaderException extends IllegalArgumentException {

    public InvalidConnectionHeaderException() {
        super("잘못된 형식의 Connection 헤더 입니다.");
    }
}
