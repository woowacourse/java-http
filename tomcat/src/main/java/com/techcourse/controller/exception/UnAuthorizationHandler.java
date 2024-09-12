package com.techcourse.controller.exception;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class UnAuthorizationHandler extends AbstractController {
    private static final UnAuthorizationHandler INSTANCE = new UnAuthorizationHandler();

    private UnAuthorizationHandler() {
    }

    public static UnAuthorizationHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().unauthorizedBuild());
    }
}
