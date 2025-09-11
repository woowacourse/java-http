package com.techcourse.controller;

import com.techcourse.controller.core.RequestController;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;

public class RequestMapping {

    private final HttpVersion httpVersion;

    public RequestMapping(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public RequestController getRequestController(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return new RootRequestController(httpVersion);
        }

        if (httpRequest.getFilePath().equals("/login.html")) {
            return new LoginRequestController(httpVersion);
        }

        if (httpRequest.getFilePath().equals("/register.html")) {
            return new RegisterRequestController(httpVersion);
        }

        return new StaticResourceRequestController(httpVersion);
    }
}
