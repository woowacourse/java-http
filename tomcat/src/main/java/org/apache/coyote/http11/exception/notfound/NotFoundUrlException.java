package org.apache.coyote.http11.exception.notfound;

public class NotFoundUrlException extends NotFoundException {

    public NotFoundUrlException() {
        super("요청한 주소를 찾을 수 없습니다.");
    }
}
