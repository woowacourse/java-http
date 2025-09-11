package org.apache.catalina.controller;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final String requestURI = request.getRequestURI();
        response.sendResource(requestURI);
    }
}
