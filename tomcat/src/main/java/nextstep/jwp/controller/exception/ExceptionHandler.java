package nextstep.jwp.controller.exception;

import servlet.mapping.ResponseEntity;

public interface ExceptionHandler {

    void service(Exception e, ResponseEntity entity);

    boolean isMapped(Exception e);
}
