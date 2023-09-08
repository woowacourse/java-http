package org.apache.coyote.httprequest.exception;

public class InvalidHostHeaderException extends IllegalArgumentException {

    public InvalidHostHeaderException() {
        super("잘못된 형식의 Host 헤더 입니다.");
    }
}
