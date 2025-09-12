package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;

public class StaticRequestController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpResponseConfigurator.okWithStaticResource(response, request.getStaticResourcePath());
    }

    @Override
    public boolean support(final HttpRequest request) {
        return request.isStaticResource();
    }
}
