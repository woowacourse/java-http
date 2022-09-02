package org.apache.coyote.exception;

public class InvalidAccountException extends RuntimeException {

    public InvalidAccountException() {
        super("존재하지 않는 회원입니다.");
    }
}
