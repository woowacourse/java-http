package com.techcourse.controller;

import org.apache.catalina.contoller.AbstractController;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

//Todo: 클래스 네이밍 변경 고려 [2025-09-05 17:19:32]
public class DefaultController extends AbstractController {

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getToDefault);
    }

    public String getToDefault(Http11Request request, Http11Response response) {
        response.setBody("Hello World!".getBytes());
        return "/";
    }
}
