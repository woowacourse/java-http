package org.apache.coyote.http11.exception;

import java.util.NoSuchElementException;

public class ResourceNotFoundException extends NoSuchElementException {

    public ResourceNotFoundException(final String filePath) {
        super("리소스를 찾을 수 없습니다 : " + filePath);
    }
}
