package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

import java.io.IOException;

public class StaticController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String requestURI = request.requestLine().path();

        if (requestURI.equals("/")) {
            response.sendForwardResponse(HttpStatusCode.OK, "/index.html");
            return;
        }

        response.sendForwardResponse(HttpStatusCode.OK, requestURI);
    }
}
