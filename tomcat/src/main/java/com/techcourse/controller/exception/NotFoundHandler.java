package com.techcourse.controller.exception;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class NotFoundHandler extends AbstractController {
    private static final NotFoundHandler INSTANCE = new NotFoundHandler();

    private NotFoundHandler() {
    }

    public static NotFoundHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().notFoundBuild());
    }
}
