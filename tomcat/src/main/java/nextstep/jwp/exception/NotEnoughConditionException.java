package nextstep.jwp.exception;

public class NotEnoughConditionException extends RequestException {

    public NotEnoughConditionException() {
        super("조건이 충분하지 않습니다.");
    }
}
