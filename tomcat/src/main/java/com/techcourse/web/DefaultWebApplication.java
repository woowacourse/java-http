package com.techcourse.web;

import com.techcourse.controller.FrontController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultWebApplication implements WebApplication {
    private final FrontController frontController;

    public DefaultWebApplication() {
        this.frontController = new FrontController();
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        return frontController.service(request);
    }

    public static WebApplication create() {
        return new DefaultWebApplication();
    }
}
