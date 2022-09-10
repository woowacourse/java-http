package nextstep.jwp.controller.exception;

import java.util.List;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.handler.ExceptionHandler;
import servlet.mapping.ResponseEntity;

public class BaseHandler implements ExceptionHandler {

    private static final List<Class<? extends Exception>> EXCEPTION_CLASS = List.of(Exception.class);


    @Override
    public ResponseEntity service() {
        return new ResponseEntity("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public List<Class<? extends Exception>> getExceptionClass() {
        return EXCEPTION_CLASS;
    }
}
