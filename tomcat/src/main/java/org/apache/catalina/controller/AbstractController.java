package org.apache.catalina.controller;

import org.apache.coyote.http11.dto.request.HttpRequest;
import org.apache.coyote.http11.dto.response.Status;


public abstract class AbstractController implements Controller {

    @Override
    public ControllerResult service(final HttpRequest request) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return doGet(request);
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            return doPost(request);
        }
        return ControllerResult.of(Status.INTERNAL_ERROR);
    }

    protected ControllerResult doGet(final HttpRequest request) {
        return ControllerResult.of(Status.NOT_FOUND);
    }

    protected ControllerResult doPost(final HttpRequest request) {
        return ControllerResult.of(Status.NOT_FOUND);
    }
}
