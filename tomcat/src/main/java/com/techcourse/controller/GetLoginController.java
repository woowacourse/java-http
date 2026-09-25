package com.techcourse.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.routing.Controller;
import org.apache.coyote.routing.RouteInfo;
import org.apache.coyote.routing.RouteKey;

public class GetLoginController implements RouteInfo, Controller {

    public String handle(final HttpRequest request, final HttpResponse response) {
        if (isLoggedIn(request)) {
            response.sendRedirect("/index.html");
            return "/index.html";
        }
        return "/login.html";
    }

    @Override
    public RouteKey getRouteKey() {
        return new RouteKey(HttpMethod.GET, "/login");
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(PostLoginController.LOGIN_USER) != null;
    }
}
