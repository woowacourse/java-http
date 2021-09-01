package nextstep.jwp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordMismatchException extends RuntimeException {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public PasswordMismatchException() {
        logger.info("암호가 일치하지 않습니다.");
    }
}
