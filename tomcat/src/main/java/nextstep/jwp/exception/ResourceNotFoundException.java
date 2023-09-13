package nextstep.jwp.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Exception e) {
        super(e);
    }
}
