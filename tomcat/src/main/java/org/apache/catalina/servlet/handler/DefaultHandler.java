package org.apache.catalina.servlet.handler;

import static org.apache.coyote.http11.common.Method.GET;

import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class DefaultHandler extends AbstractHandler {

    @Override
    protected Response doGet(final Request request) {
        return ViewController.resource(request);
    }

    @Override
    public boolean canHandle(final Request request) {
        return GET == request.getMethod();
    }

}
