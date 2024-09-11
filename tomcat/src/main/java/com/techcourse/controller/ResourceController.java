package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            response.setResource(request.getPath());
        } catch (final Exception e) {
            response.setResource("404.html");
        }
    }
}
