package nextstep.jwp.controller;

public class GreetingController implements Controller {

    @Override
    public ResponseEntity execute(final String target) throws Exception {
        return new ResponseEntity(HttpStatus.OK, "text/html", "Hello world!");
    }
}
