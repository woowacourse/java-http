package nextstep.jwp.controller;

import org.apache.coyote.http11.message.request.HttpRequest;

public interface Controller {

    boolean canHandle(HttpRequest request);

    ResponseEntity handle(HttpRequest request);
}
