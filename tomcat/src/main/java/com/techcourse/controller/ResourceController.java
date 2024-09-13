package com.techcourse.controller;

import org.apache.coyote.http11.file.NoResourceFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {

    private static final String NOT_FOUND_URI = "/404";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            String uriPath = request.getPath();
            response.addStaticResource(uriPath);
        } catch (NoResourceFoundException e) {
            response.sendRedirect(NOT_FOUND_URI);
        }
    }
}
