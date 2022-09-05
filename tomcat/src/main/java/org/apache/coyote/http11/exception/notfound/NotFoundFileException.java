package org.apache.coyote.http11.exception.notfound;

public class NotFoundFileException extends NotFoundException {

    public NotFoundFileException() {
        super("요청한 파일을 찾을 수 없습니다.");
    }
}
