package org.apache.coyote.http11.request.exception;

public class HttpMethodNotAllowedException extends RuntimeException {

    public HttpMethodNotAllowedException() {
        super("허용되지 않는 HTTP Method 입니다.");
    }
}
