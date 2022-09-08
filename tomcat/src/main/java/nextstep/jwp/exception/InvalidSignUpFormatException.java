package nextstep.jwp.exception;

public class InvalidSignUpFormatException extends RuntimeException {

    public InvalidSignUpFormatException() {
        super("회원가입에 필요한 정보가 누락되었습니다.");
    }
}
