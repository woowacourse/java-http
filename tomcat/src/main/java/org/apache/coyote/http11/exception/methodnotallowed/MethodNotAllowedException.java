package org.apache.coyote.http11.exception.methodnotallowed;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException() {
        super("해당 Http Method가 지원되지 않습니다.");
    }
}
