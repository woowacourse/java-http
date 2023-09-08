package org.apache.coyote.http11.exception;

public class NotCorrectPasswordException extends RuntimeException {
    public NotCorrectPasswordException() {
        super("패스워드가 일치하지 않습니다.");
    }
}
