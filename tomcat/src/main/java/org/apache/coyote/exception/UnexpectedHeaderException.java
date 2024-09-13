package org.apache.coyote.exception;

public class UnexpectedHeaderException extends RuntimeException {

    public static final String MESSAGE = "요청 헤더가 존재하지 않습니다. \n요청 내용:\n";

    public UnexpectedHeaderException(String headerName) {
        super(MESSAGE + headerName);
    }
}
