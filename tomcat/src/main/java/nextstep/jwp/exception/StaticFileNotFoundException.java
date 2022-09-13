package nextstep.jwp.exception;

import org.apache.coyote.exception.NotFoundException;

public class StaticFileNotFoundException extends NotFoundException {
    public StaticFileNotFoundException(String filePath) {
        super("파일을 찾지 못했습니다. " + filePath);
    }
}
