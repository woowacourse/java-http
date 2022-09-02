package nextstep.jwp.exception;

public class StaticFileNotFoundException extends NotFoundException {
    public StaticFileNotFoundException(String filePath) {
        super("파일을 찾지 못했습니다. " + filePath);
    }
}
