package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class NotFoundException extends HttpStatusException {

    public NotFoundException() {
        super(HttpStatusCode.NOT_FOUND, "요청 정보를 찾을 수 없습니다.");
    }
}
