package org.apache.coyote.http11.exception.unauthorized;

public class LoginFailException extends UnAuthorizedException {

    public LoginFailException() {
        super("로그인에 실패했습니다.");
    }
}
