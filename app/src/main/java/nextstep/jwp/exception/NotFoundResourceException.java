package nextstep.jwp.exception;

public class NotFoundResourceException extends RuntimeException {

    private static final String MESSAGE = "해당 Resource를 찾지 못했습니다. resource name: ";

    public NotFoundResourceException(String resourceName) {
        super(MESSAGE + resourceName);
    }
}
