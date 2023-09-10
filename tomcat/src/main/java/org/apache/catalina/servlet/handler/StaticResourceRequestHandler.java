package org.apache.catalina.servlet.handler;

import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public Response service(final Request request) {
        return ViewController.resource(request);
    }

}
