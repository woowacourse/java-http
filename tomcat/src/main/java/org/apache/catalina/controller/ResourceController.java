package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class ResourceController extends HttpController {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return "GET".equals(httpRequest.getMethod());
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        handleResource(httpRequest.getTarget(), httpRequest, httpResponse);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
