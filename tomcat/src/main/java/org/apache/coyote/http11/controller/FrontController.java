package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private FrontController() {
    }

    public static void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Controller controller = RequestMapping.getController(httpRequest.getHttpStartLine());
        controller.service(httpRequest, httpResponse);
    }
}
