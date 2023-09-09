package org.apache.coyote.controller.exception;

public class UnsupportedRequestMethodException extends IllegalArgumentException {

    public UnsupportedRequestMethodException() {
        super("지원하지 않는 Request Method 입니다.");
    }
}
