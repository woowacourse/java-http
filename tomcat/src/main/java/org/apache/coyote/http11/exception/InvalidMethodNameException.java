package org.apache.coyote.http11.exception;

public class InvalidMethodNameException extends RuntimeException {

    public InvalidMethodNameException(final String invalidMethodName) {
        super("존재하지 않는 메서드입니다. method : " + invalidMethodName);
    }
}
