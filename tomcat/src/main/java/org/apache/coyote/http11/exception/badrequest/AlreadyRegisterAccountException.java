package org.apache.coyote.http11.exception.badrequest;

public class AlreadyRegisterAccountException extends BadRequestException {

    public AlreadyRegisterAccountException() {
        super("이미 가입된 계정입니다.");
    }
}
