package org.apache.coyote.exception;

public class NoSuchHttpMethodException extends RuntimeException {

    public static final String NO_SUCH_HTTP_METHOD_MESSAGE = "해당 Http Method는 존재하지 않습니다.";

    public NoSuchHttpMethodException() {
        super(NO_SUCH_HTTP_METHOD_MESSAGE);
    }
}
