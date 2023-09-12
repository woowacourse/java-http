package org.apache.coyote.exception;

import org.apache.coyote.response.Status;

public class UnauthorizedException extends HttpException {

    public UnauthorizedException(final String account) {
        this.status = Status.UNAUTHORIZED;
        this.message = "인증에 실패하였습니다 :" + account;
    }
}
