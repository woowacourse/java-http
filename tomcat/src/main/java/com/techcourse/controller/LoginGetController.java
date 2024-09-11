package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.ViewHttpHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginGetController implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginGetController.class);

    public LoginGetController() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return new ViewHttpHandler("login").handle(request);
    }
}
