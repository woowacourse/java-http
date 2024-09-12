package com.techcourse.controller.exception;

import org.apache.coyote.controller.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class UnAuthorizationController extends AbstractController {
    private static final UnAuthorizationController INSTANCE = new UnAuthorizationController();

    private UnAuthorizationController() {
    }

    public static UnAuthorizationController getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().unauthorizedBuild());
    }
}
