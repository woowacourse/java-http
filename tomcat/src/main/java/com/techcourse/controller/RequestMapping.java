package com.techcourse.controller;

import com.techcourse.controller.core.RequestController;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;

public class RequestMapping {

    private final RootRequestController rootRequestController;
    private final LoginRequestController loginRequestController;
    private final RegisterRequestController registerRequestController;
    private final StaticResourceRequestController staticResourceRequestController;

    public RequestMapping(final HttpVersion httpVersion) {
        this.rootRequestController = new RootRequestController(httpVersion);
        this.loginRequestController = new LoginRequestController(httpVersion);
        this.registerRequestController = new RegisterRequestController(httpVersion);
        this.staticResourceRequestController = new StaticResourceRequestController(httpVersion);
    }

    public RequestController getRequestController(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return rootRequestController;
        }

        if (httpRequest.getFilePath().equals("/login.html")) {
            return loginRequestController;
        }

        if (httpRequest.getFilePath().equals("/register.html")) {
            return registerRequestController;
        }

        return staticResourceRequestController;
    }
}
