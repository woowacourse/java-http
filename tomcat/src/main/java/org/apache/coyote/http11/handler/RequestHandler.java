package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RequestHandler {

    public static HttpResponse handle(
            final Controller controller,
            final HttpRequest httpRequest,
            final HttpResponse httpResponse) throws IOException {
        return controller.service(httpRequest, httpResponse);
    }
}
