package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.routing.Controller;
import org.apache.catalina.routing.RouteInfo;
import org.apache.catalina.routing.RouteKey;

public class HelloWorldController implements Controller, RouteInfo {

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        return "hello world";
    }

    @Override
    public RouteKey getRouteKey() {
        return new RouteKey(HttpMethod.GET,"/");
    }
}
