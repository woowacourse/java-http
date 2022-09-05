package nextstep.jwp.exception;

public class FileAccessException extends RuntimeException {

    public FileAccessException() {
        super("파일 접근에서 예외가 발생했습니다.");
    }
}
