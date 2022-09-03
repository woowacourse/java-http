package org.apache.coyote.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String uri) {
        super(String.format("%s 경로의 파일을 찾을 수 없습니다.", uri));
    }
}
