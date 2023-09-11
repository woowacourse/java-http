package nextstep.jwp.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String url) {
        super(url + "을 찾을 수 없습니다.");
    }
}
