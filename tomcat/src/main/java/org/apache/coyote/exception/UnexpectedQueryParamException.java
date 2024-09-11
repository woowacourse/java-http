package org.apache.coyote.exception;

public class UnexpectedQueryParamException extends RuntimeException {

    public static final String MESSAGE = "존재하지 않는 Query Param입니다.";

    public UnexpectedQueryParamException() {
        super(MESSAGE);
    }
}
