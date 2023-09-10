package nextstep.jwp.exception;

public class MemberExistException extends RuntimeException {

    public MemberExistException() {
        super("이미 가입된 회원입니다.");
    }
}
