package org.apache.coyote.http11;

public class LoginFailureException extends RuntimeException{

    private static final String ERROR_MESSAGE = "로그인 정보가 잘못됐다.";

    public LoginFailureException() {
        super(ERROR_MESSAGE);
    }
}
