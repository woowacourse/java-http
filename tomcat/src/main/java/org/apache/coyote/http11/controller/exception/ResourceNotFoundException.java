package org.apache.coyote.http11.controller.exception;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {

    public ResourceNotFoundException(final String pathName) {
        super(pathName +"에 위치한 리소스를 찾을 수 없습니다.");
    }
}
