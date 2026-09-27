package com.techcourse.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.routing.Controller;
import org.apache.catalina.routing.RouteInfo;
import org.apache.catalina.routing.RouteKey;

public class GetLoginController implements RouteInfo, Controller {

    public String handle(final HttpRequest request, final HttpResponse response) {
        if (isLoggedIn(request)) {
            response.sendRedirect("/index.html");
            return "로그인이 되어있어 기본 페이지로 이동합니다.";
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
