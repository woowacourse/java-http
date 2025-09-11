package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class InternalServerErrorException extends HttpStatusException {

    public InternalServerErrorException() {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, "서버에서 예상치 못한 문제가 발생했습니다.");
    }
}
