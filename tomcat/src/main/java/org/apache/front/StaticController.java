package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.*;

public class StaticController implements FrontController {

    private static final StaticController staticController = new StaticController();

    private StaticController(){
    }

    public static StaticController singleTone() {
        return staticController;
    }

    @Override
    public ResponseEntity process(final Request request) {
        return ResponseEntity.fromStatic(request, ResponseStatus.OK);
    }
}
