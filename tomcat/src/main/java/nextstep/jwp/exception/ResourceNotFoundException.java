package nextstep.jwp.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String resourcePath) {
        super(resourcePath + "을 찾을 수 없습니다.");
    }
}
