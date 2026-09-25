package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.routing.Controller;
import org.apache.coyote.routing.RouteInfo;
import org.apache.coyote.routing.RouteKey;

public class GetRegisterController implements Controller, RouteInfo {
    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        return "/register.html";
    }

    @Override
    public RouteKey getRouteKey() {
        return new RouteKey(HttpMethod.GET, "/register");
    }
}
