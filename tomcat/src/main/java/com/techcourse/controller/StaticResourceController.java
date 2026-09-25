package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private final ResourceRenderer renderer = new ResourceRenderer();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        renderer.render(request.getPath(), response);
    }
}
