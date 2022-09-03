package nextstep.jwp.exception;

public class MemberNotFoundException extends IllegalArgumentException {

    public MemberNotFoundException() {
        super("일치하는 회원 정보가 없습니다.");
    }
}
