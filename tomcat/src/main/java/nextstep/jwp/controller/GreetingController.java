package nextstep.jwp.controller;

public class GreetingController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) throws Exception {
        return new ResponseEntity(HttpStatus.OK, request.getContentType(), "Hello world!");
    }
}
