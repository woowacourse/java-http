package nextstep.jwp.exception;

import org.apache.coyote.http.HttpStatus;

public class DuplicateUserException extends CustomException{

    public DuplicateUserException() {
        super(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다.");
    }
}
