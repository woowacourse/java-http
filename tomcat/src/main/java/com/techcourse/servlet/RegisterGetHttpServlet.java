package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.StaticResourceServer;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterGetHttpServlet implements HttpServlet {

    @Override
    public boolean canService(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/register");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        StaticResourceServer.load(response, "/register.html");
    }
}
