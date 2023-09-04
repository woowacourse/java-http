package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.controller.rest.ResponseEntity;
import org.apache.coyote.http11.HttpRequest;

public interface Controller {

    boolean canHandle(HttpRequest request);

    ResponseEntity handle(HttpRequest request) throws IOException;
}
