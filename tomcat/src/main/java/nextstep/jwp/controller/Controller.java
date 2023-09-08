package nextstep.jwp.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;

public interface Controller {

    ResponseEntity handle(Request request);
}
