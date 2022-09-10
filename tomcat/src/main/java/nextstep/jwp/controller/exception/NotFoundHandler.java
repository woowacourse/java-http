package nextstep.jwp.controller.exception;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.handler.ExceptionHandler;
import servlet.mapping.ResponseEntity;

public class NotFoundHandler implements ExceptionHandler {

    private static final List<Class<? extends Exception>> EXCEPTION_CLASS = List.of(NoSuchElementException.class);

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/404.html", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Class<? extends Exception>> getExceptionClass() {
        return EXCEPTION_CLASS;
    }
}
