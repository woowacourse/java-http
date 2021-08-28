package nextstep.jwp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordNotMatchException extends RuntimeException {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public PasswordNotMatchException() {
        logger.info("암호가 일치하지 않습니다.");
    }
}
