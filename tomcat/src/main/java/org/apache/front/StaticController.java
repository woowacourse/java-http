package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.*;

public class StaticController implements FrontController {

    @Override
    public ResponseEntity process(final Request request) {
        return ResponseEntity.fromStatic(request, ResponseStatus.OK);
    }
}
