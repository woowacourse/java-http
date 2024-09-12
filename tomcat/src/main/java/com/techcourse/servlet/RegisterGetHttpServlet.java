package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;

public class RegisterGetHttpServlet implements HttpServlet {

    @Override
    public boolean canService(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/register");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        // TODO: 404 on ResourceNotFound
        byte[] body = StaticResourceLoader.load("/register.html");
        response.setContentType(ContentType.TEXT_HTML);
        response.ok(body);
    }
}
