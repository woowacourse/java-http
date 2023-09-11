package nextstep.jwp.exception;

import org.apache.catalina.exception.CustomBadRequestException;

public class UserAlreadyExistException extends CustomBadRequestException {

    public UserAlreadyExistException(final String account) {
        super("이미 등록된 사용자며입니다. 입력된 사용자 명 : " + account);
    }
}
