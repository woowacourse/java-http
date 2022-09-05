package org.apache.coyote.http11.exception;

public class NotFoundFileException extends RuntimeException {

    public NotFoundFileException() {
        super("요청한 파일을 찾을 수 없습니다.");
    }
}
