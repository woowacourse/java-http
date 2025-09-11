package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class HomeController extends AbstractController {

    public static final String ENDPOINT = "/";

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getToHome);
    }

    public String getToHome(Http11Request request, Http11Response response) {
        response.setBody("Hello World!".getBytes());
        return ENDPOINT;
    }
}
