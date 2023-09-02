package org.apache.coyote.http11.request;

public class InvalidHeaderException extends RuntimeException {

    public InvalidHeaderException() {
        super("Request 의 Header 가 올바르지 않습니다.");
    }
}
