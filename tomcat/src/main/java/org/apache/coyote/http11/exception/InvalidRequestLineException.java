package org.apache.coyote.http11.exception;

public class InvalidRequestLineException extends RuntimeException {
    public InvalidRequestLineException(String requestLine) {
        super("잘못된 요청 라인: " + requestLine);
    }
}
