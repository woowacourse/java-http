package nextstep.jwp.exception;

public class InternalServerError extends RuntimeException {
    private static final String MSG = "서버 에러 - 500";

    public InternalServerError() {
        super(MSG);
    }
}
