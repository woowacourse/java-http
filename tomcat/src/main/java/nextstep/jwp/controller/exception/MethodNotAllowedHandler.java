package nextstep.jwp.controller.exception;

import java.util.List;
import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.handler.ExceptionHandler;
import servlet.mapping.ResponseEntity;

public class MethodNotAllowedHandler implements ExceptionHandler {

    private static final List<Class<? extends Exception>> EXCEPTION_CLASS = List.of(MethodNotAllowedException.class);

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/405.html", HttpStatus.METHOD_NOT_ALLOWED));
    }

    @Override
    public List<Class<? extends Exception>> getExceptionClass() {
        return EXCEPTION_CLASS;
    }
}
