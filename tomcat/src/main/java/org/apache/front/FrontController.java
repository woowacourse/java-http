package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;

public interface FrontController {

    ResponseEntity process(Request request);
}
