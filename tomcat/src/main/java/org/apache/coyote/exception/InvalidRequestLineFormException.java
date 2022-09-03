package org.apache.coyote.exception;

public class InvalidRequestLineFormException extends RuntimeException {

    public InvalidRequestLineFormException() {
        super("잘못된 형식의 Request Line입니다.");
    }
}
