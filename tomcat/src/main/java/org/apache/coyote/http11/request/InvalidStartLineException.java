package org.apache.coyote.http11.request;

public class InvalidStartLineException extends RuntimeException {

    public InvalidStartLineException() {
        super("Request 의 Start Line 이 올바르지 않습니다.");
    }
}
