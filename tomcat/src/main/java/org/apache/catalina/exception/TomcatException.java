package org.apache.catalina.exception;

public class TomcatException extends RuntimeException {

    private static final String MESSAGE = "톰캣 실행 중 오류가 발생했습니다.";

    public TomcatException() {
        super(MESSAGE);
    }

    public TomcatException(String message) {
        super(message);
    }
}
