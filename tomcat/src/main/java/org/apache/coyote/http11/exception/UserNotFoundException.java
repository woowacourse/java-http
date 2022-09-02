package org.apache.coyote.http11.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String account) {
        super(account + " 유저를 찾을 수 없습니다.");
    }
}
