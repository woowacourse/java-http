package nextstep.jwp.exception;

public class NotValidateHttpResponse extends RuntimeException {

    private static final String MESSAGE = "유효하지 않은 HttpResponse 입니다.";

    public NotValidateHttpResponse() {
        super(MESSAGE);
    }
}
