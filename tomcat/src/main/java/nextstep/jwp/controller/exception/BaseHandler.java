package nextstep.jwp.controller.exception;

import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class BaseHandler implements ExceptionHandler {

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/500.html", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public boolean isMapped(Exception e) {
        return true;
    }
}
