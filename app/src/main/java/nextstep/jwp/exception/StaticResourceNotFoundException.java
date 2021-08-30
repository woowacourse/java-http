package nextstep.jwp.exception;

public class StaticResourceNotFoundException extends RuntimeException {

    public StaticResourceNotFoundException(String path) {
        super(String.format("%s (을)를 찾을 수 없습니다.", path));
    }
}
