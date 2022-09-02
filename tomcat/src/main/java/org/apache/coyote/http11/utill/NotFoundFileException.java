package org.apache.coyote.http11.utill;

public class NotFoundFileException extends RuntimeException {

    public NotFoundFileException(final String message) {
        super(message);
    }

    public NotFoundFileException() {
        this("찾을 수 없는 file name 입니다.");
    }
}
