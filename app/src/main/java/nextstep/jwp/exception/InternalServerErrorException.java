package nextstep.jwp.exception;

public class InternalServerErrorException extends RuntimeException {
    private static final String MSG = "서버 에러 - 500";

    public InternalServerErrorException() {
        super(MSG);
    }
}
