package org.apache.coyote.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final String MESSAGE = ": 존재하지 않는 파일입니다.";

    public ResourceNotFoundException(String fileName) {
        super(fileName + MESSAGE);
    }
}
