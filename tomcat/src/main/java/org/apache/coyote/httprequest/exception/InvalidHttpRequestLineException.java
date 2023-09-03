package org.apache.coyote.httprequest.exception;

public class InvalidHttpRequestLineException extends IllegalArgumentException {

    public InvalidHttpRequestLineException() {
        super("잘못된 Request line 입니다.");
    }
}
