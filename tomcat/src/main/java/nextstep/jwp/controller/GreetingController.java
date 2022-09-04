package nextstep.jwp.controller;

import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

public class GreetingController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        return new ResponseEntity().contentType("text/html")
                .content("Hello world!");
    }
}
