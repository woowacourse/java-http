package com.techcourse.controller.exception;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class NotFoundController extends AbstractController {
    private static final NotFoundController INSTANCE = new NotFoundController();

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().notFoundBuild());
    }
}
