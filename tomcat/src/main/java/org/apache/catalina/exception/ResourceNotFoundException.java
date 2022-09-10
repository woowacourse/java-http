package org.apache.catalina.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String target) {
        super(target + "자원을 찾지 못했음");
    }
}
