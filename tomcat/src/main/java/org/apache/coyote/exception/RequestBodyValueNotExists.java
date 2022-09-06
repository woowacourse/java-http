package org.apache.coyote.exception;

public class RequestBodyValueNotExists extends RuntimeException {

    public RequestBodyValueNotExists() {
        super("해당 바디 값을 찾을 수 없습니다.");
    }
}
