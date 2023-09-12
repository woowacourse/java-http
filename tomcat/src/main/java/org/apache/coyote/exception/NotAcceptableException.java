package org.apache.coyote.exception;

import static org.apache.coyote.response.Status.NOT_ACCEPTABLE;

public class NotAcceptableException extends HttpException {

    public NotAcceptableException(final String header) {
        this.status = NOT_ACCEPTABLE;
        this.message = "요청하신 헤더를 사용할 수 없습니다 :" + header;
    }
}
