package nextstep.jwp.exception;

import org.apache.coyote.http.HttpStatus;

public class UnauthorizedUserException extends CustomException {

    public UnauthorizedUserException(){
        super(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다.");
    }
}
