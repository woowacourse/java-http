package nextstep.handler.util.exception;

public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException() {
        super("지정한 리소스가 존재하지 않습니다.");
    }
}
