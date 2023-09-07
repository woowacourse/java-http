package org.apache.coyote.http11.exception;

public class NotExistFileException extends RuntimeException {

    public NotExistFileException() {
        super("존재하지 않는 페이지입니다.");
    }
}
