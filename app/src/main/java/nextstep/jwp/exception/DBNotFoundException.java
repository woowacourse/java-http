package nextstep.jwp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBNotFoundException extends RuntimeException {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public DBNotFoundException() {
        logger.info("DB 에 조회된 결과가 없습니다.");
    }
}
