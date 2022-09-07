package nextstep.jwp.controller.exception;

import java.util.NoSuchElementException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class NotFoundHandler implements ExceptionHandler {

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/404.html", HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean isMapped(Exception e) {
        try {
            throw e;
        } catch (NoSuchElementException exception) {
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
