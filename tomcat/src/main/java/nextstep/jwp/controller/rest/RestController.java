package nextstep.jwp.controller.rest;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

public interface RestController extends Controller {

    @Override
    boolean canHandle(HttpRequest request);

    @Override
    ResponseEntity handle(HttpRequest request);
}
