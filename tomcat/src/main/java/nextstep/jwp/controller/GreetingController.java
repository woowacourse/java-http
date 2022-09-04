package nextstep.jwp.controller;

import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

public class GreetingController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        return new ResponseEntity(HttpStatus.OK, "text/html", "Hello world!");
    }
}
