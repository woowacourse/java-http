package org.apache.coyote.httprequest.exception;

public class HttpMessageNotReadableException extends IllegalArgumentException {

    public HttpMessageNotReadableException() {
        super("잘못된 Request Method 입니다.");
    }
}
