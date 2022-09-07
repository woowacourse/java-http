package nextstep.jwp.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "리소스를 찾을 수 없습니다.";

    public ResourceNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
