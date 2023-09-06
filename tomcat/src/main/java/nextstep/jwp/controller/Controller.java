package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;

public interface Controller {

    ResponseEntity service(final HttpRequest httpRequest) throws IOException;

    boolean canHandle(final HttpRequest httpRequest);
}
