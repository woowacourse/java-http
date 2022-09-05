package nextstep.jwp.exception;

public class StreamException extends RuntimeException {

    public StreamException() {
        super("Stream을 가져오는데에 예외가 발생했습니다.");
    }
}
