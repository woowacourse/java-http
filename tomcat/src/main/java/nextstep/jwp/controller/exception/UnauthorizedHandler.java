package nextstep.jwp.controller.exception;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.handler.ExceptionHandler;
import servlet.mapping.ResponseEntity;

public class UnauthorizedHandler implements ExceptionHandler {

    private static final List<Class<? extends Exception>> EXCEPTION_CLASS = List.of(
            NoUserException.class,
            InvalidPasswordException.class);

    @Override
    public ResponseEntity service() {
        return new ResponseEntity("/401.html", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public List<Class<? extends Exception>> getExceptionClass() {
        return EXCEPTION_CLASS;
    }
}
