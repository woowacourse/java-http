package nextstep.jwp;

public class InvalidHttpMethodException extends RuntimeException {

    public InvalidHttpMethodException(String name) {
        super("유효하지 않은 HTTP 메서드입니다 : " + name);
    }
}
