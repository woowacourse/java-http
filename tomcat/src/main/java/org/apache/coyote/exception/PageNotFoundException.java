package org.apache.coyote.exception;

import static org.apache.coyote.response.Status.NOT_FOUND;

public class PageNotFoundException extends HttpException {

    public PageNotFoundException(final String path) {
        this.status = NOT_FOUND;
        this.message = "요청하신 페이지를 찾을 수 없습니다 :" + path;
    }
}
