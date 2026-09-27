package com.techcourse.controller;

import org.apache.catalina.routing.RequestHandler;
import org.apache.catalina.routing.requestMapping.RequestMapping;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloWorldRequestHandler implements RequestHandler {

    @Override
    @RequestMapping(method = HttpMethod.GET, path = "/")
    public String handle(final HttpRequest request, final HttpResponse response) {
        return "hello world";
    }
}
