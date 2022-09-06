package nextstep.jwp.controller;

import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class ExceptionHandler {

    public ExceptionHandler() {
    }

    public ResponseEntity notFound() {
        return new ResponseEntity("/404.html", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity unauthorized() {
        return new ResponseEntity("/401.html", HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity internalServerError() {
        return new ResponseEntity("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
