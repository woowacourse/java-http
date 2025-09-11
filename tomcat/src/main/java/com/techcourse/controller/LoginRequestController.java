package com.techcourse.controller;

import org.apache.catalina.Controller;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestMethod;

import java.net.URL;
import java.nio.file.Path;

public class LoginRequestController implements Controller {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl()
                        .startsWith("/login");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Session session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            httpResponse.redirect("http://localhost:8080/index.html");
            return;
        }

        URL resource = getClass().getClassLoader()
                .getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());

        httpResponse.ok()
                .writeStaticResource(resourcePath);
    }
}
