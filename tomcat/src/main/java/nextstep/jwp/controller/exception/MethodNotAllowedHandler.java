package nextstep.jwp.controller.exception;

import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class MethodNotAllowedHandler implements ExceptionHandler {

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/405.html", HttpStatus.METHOD_NOT_ALLOWED));
    }

    @Override
    public boolean isMapped(Exception e) {
        try {
            throw e;
        } catch (MethodNotAllowedException exception) {
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
