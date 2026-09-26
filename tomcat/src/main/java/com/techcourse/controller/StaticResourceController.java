package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    public StaticResourceController() {
        super("GET");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.forward(request.getPath());
    }
}
