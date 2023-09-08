package org.apache.coyote.http11.exception;

public class InvalidRequestLineException extends RuntimeException {
    public InvalidRequestLineException() {
        super("잘못된 RequestURL입니다.");
    }
}
