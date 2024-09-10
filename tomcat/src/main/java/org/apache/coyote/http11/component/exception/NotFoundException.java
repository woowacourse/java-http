package org.apache.coyote.http11.component.exception;

public class NotFoundException extends Exception {

    public NotFoundException() {
        super("자원을 찾을 수 없음");
    }
}
