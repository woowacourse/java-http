package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {
    private static final String STATIC_PATH = "static";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String responseBody = ResourceReader.readResource(STATIC_PATH + request.getAbsolutePath());
        response.getResponse(request, responseBody);
    }
}
