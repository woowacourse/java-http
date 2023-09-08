package nextstep.jwp.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.coyote.response.ResponseStatus;

public class HelloWorldController implements Controller{

    @Override
    public ResponseEntity handle(Request request) {
        return ResponseEntity.fromString(request, "Hello world!", ResponseStatus.OK);
    }
}
