package nextstep.jwp.exception;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {

    public UserNotFoundException(final String account) {
        super("회원을 조회할 수 없습니다 : " + account);
    }
}
