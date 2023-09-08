package org.apache.coyote.http11.handler.exception;

public class HttpMethodInvalidException extends RuntimeException {

    public HttpMethodInvalidException(final String method) {
        super(method + " -> 입력하신 Http Method가 잘못되었습니다.");
    }
}
