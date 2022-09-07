package org.apache.coyote.http11.handler;

import nextstep.jwp.presentation.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseEntity;

public class RequestHandler {

    public static ResponseEntity handle(
            final Controller controller,
            final HttpRequest httpRequest,
            final HttpResponse httpResponse) {
        return controller.service(httpRequest, httpResponse);
    }
}
