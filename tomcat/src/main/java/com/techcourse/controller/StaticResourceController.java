package com.techcourse.controller;

import com.techcourse.web.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String uriPath = request.getRequestLine().getTarget().split("\\?", 2)[0];
        response.copyFrom(staticResourceHandler.handle(uriPath));
    }
}
