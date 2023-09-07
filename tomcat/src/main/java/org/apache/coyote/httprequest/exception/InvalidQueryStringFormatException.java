package org.apache.coyote.httprequest.exception;

public class InvalidQueryStringFormatException extends IllegalArgumentException {

    public InvalidQueryStringFormatException() {
        super("잘못된 쿼리 스트링 형식입니다.");
    }
}
