package org.apache.catalina.exception;

import nextstep.jwp.exception.NotFoundException;

public class RequestBodyValueNotExists extends NotFoundException {

    public RequestBodyValueNotExists() {
        super("해당 바디 값을 찾을 수 없습니다.");
    }
}
