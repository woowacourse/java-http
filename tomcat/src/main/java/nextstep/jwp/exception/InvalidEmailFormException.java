package nextstep.jwp.exception;

public class InvalidEmailFormException extends RuntimeException {

    public InvalidEmailFormException(final String email) {
        super("이메일 형식이 잘못 됐습니다. 입력하신 이메일 : " + email);
    }
}
