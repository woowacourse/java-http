package com.techcourse.controller.exception;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class InternalServerErrorHandler extends AbstractController {
    private static final InternalServerErrorHandler INSTANCE = new InternalServerErrorHandler();

    private InternalServerErrorHandler() {
    }

    public static InternalServerErrorHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().internalServerErrorBuild());
    }
}

