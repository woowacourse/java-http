package nextstep.jwp.exception;

public class NotFoundParamException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 Param입니다. param key : ";

    public NotFoundParamException(String key) {
        super(MESSAGE + key);
    }
}
