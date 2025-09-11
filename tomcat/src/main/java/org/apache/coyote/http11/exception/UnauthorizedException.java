package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class UnauthorizedException extends HttpStatusException {

    public UnauthorizedException() {
        super(HttpStatusCode.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }
}
