package org.apache.coyote.httprequest.exception;

public class InvalidHttpMethodException extends IllegalArgumentException {

    public InvalidHttpMethodException() {
        super("잘못된 Request Method 입니다.");
    }
}
