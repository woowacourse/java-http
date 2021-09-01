package nextstep.jwp.exception;

public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException() {
        super("요청은 잘 만들어졌지만, 문법 오류로 인하여 따를 수 없습니다.");
    }
}
