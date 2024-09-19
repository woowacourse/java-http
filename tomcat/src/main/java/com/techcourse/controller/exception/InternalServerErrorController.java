package com.techcourse.controller.exception;

import org.apache.coyote.controller.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class InternalServerErrorController extends AbstractController {

    private static final InternalServerErrorController INSTANCE = new InternalServerErrorController();

    private InternalServerErrorController() {
    }

    public static InternalServerErrorController getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().internalServerErrorBuild());
    }
}

