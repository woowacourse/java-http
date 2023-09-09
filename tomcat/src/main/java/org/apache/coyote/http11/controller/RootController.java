package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {
    private static final String ROOT_RESOURCE = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.getResponse(request, ROOT_RESOURCE);
    }
}
