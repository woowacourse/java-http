package org.apache.coyote.http11.exception.unauthorised;

public class LoginFailException extends UnAuthorisedException {

    public LoginFailException() {
        super("로그인에 실패했습니다.");
    }
}
