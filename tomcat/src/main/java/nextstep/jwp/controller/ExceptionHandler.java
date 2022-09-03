package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class ExceptionHandler {

    public ExceptionHandler() {
    }

    public ResponseEntity notFound() {
        return new ResponseEntity(GET, "/404.html", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity unauthorized() {
        return new ResponseEntity(GET, "/401.html", HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity internalServerError() {
        return new ResponseEntity(GET, "/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
