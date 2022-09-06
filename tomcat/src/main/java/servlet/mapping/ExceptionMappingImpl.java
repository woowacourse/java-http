package servlet.mapping;

import java.util.NoSuchElementException;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionMappingImpl implements ExceptionMapping {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMappingImpl.class);

    private final ExceptionHandler exceptionHandler;

    public ExceptionMappingImpl(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public ResponseEntity map(Exception exception) {
        LOG.error(exception.getMessage(), exception);
        try {
            throw exception;
        } catch (NoSuchElementException e) {
            return exceptionHandler.notFound();
        } catch (NoUserException | InvalidPasswordException e) {
            return exceptionHandler.unauthorized();
        } catch (Exception e) {
            return exceptionHandler.internalServerError();
        }
    }
}
