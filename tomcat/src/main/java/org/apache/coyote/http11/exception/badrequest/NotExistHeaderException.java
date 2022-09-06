package org.apache.coyote.http11.exception.badrequest;

public class NotExistHeaderException extends BadRequestException {

    public NotExistHeaderException() {
        super("요청한 헤더가 없습니다.");
    }
}
