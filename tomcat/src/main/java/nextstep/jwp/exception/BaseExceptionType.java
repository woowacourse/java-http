package nextstep.jwp.exception;

import org.apache.coyote.http11.HttpStatus;

public interface BaseExceptionType {

    HttpStatus httpStatus();

    String errorMessage();
}
