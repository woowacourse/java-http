package org.apache.coyote.http11.exception;

public class ResourceNotFoundException extends RuntimeException{
    private static final String MESSAGE = "유효하지 않은 리소스 접근입니다.";

    public ResourceNotFoundException() {
        super(MESSAGE);
    }
}
