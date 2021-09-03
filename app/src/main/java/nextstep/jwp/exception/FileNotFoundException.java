package nextstep.jwp.exception;

public class FileNotFoundException extends RuntimeException {

    private static final String MESSAGE = "파일을 찾을 수 없습니다.";

    public FileNotFoundException() {
        super(MESSAGE);
    }
}
