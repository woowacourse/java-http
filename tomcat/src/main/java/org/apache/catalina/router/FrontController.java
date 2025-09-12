package org.apache.catalina.router;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final RequestMapping requestMapping;

    public FrontController(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void dispatch(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
