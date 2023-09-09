package nextstep.jwp.exception;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(final String account) {
        super("이미 등록된 사용자며입니다. 입력된 사용자 명 : " + account);
    }
}
