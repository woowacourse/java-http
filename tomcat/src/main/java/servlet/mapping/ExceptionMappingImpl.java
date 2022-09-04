package servlet.mapping;

import java.util.NoSuchElementException;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionMappingImpl implements ExceptionMapping {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    private final ExceptionHandler exceptionHandler;

    public ExceptionMappingImpl(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public ResponseEntity map(Exception exception) {
        LOG.error(exception.getMessage(), exception);
        Class<? extends Exception> exceptionClass = exception.getClass();

        if (exceptionClass.isInstance(NoSuchElementException.class)) {
            return exceptionHandler.notFound();
        }
        if (exceptionClass.isInstance(NoUserException.class) ||
                exceptionClass.isInstance(InvalidPasswordException.class)) {
            return exceptionHandler.unauthorized();
        }
        return exceptionHandler.internalServerError();
    }
}
