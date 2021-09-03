package nextstep.jwp.exception;

public class NotFoundException extends RuntimeException {

    private static final String MESSAGE = "/404.html";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
