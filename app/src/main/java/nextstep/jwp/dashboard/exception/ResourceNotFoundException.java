package nextstep.jwp.dashboard.exception;

public class ResourceNotFoundException extends NotFoundException {

    public ResourceNotFoundException(String resourceName) {
        super(String.format("Unable to find resource : %s", resourceName));
    }
}
