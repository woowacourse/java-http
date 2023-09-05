package nextstep.jwp.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("해당 페이지를 찾을 수 없습니다.");
    }
}
