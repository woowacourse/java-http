package nextstep.jwp.controller.exception;

import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class UnauthorizedHandler implements ExceptionHandler {

    @Override
    public void service(Exception e, ResponseEntity entity) {
        entity.clone(new ResponseEntity("/401.html", HttpStatus.UNAUTHORIZED));
    }

    @Override
    public boolean isMapped(Exception e) {
        try {
            throw e;
        } catch (NoUserException | InvalidPasswordException exception) {
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
