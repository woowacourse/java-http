package com.techcourse.controller;

import java.util.function.Function;

import org.apache.coyote.http11.application.Handler;
import org.apache.coyote.http11.request.Api;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloController implements Handler {

    @Override
    public Function<HttpRequest, HttpResponse> getHandlerMethod(Api requestApi) {
        if (requestApi.path().equals("/") || requestApi.path().equals("\\")) {
            return this::hello;
        }
        return null;
    }

    public HttpResponse hello(HttpRequest request) {
        request.setPath("/hello.html");
        return new HttpResponse();
    }
}
