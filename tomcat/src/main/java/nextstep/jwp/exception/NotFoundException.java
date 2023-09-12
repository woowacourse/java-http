package nextstep.jwp.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("요청하신 페이지를 찾을 수 없습니다.");
    }
}
