package nextstep.jwp.exception;

public class NotAllowedExtensionException extends RuntimeException {

    public NotAllowedExtensionException() {
        super("해당하는 Extension이 존재하지 않습니다.");
    }
}
