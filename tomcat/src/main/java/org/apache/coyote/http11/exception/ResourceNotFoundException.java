package org.apache.coyote.http11.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String message) {
        super(String.format("%s 경로의 파일을 찾을 수 없습니다.", message));
    }
}
