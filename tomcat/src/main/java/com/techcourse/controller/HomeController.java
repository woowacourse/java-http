package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        Session session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        if (loggedIn) {
            response.redirect("/index.html");
        } else {
            response.writeText("Hello world!", "text/html;charset=utf-8");
        }
    }
}
