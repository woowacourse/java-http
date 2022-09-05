package org.apache.coyote.http11;

import java.io.IOException;

import nextstep.jwp.presentation.Controller;

public class ResponseHandler {

    public static ResponseEntity handle(final Controller controller, final String startLine) throws IOException {
        return controller.run(startLine);
    }
}
