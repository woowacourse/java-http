package com.techcourse.handler;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundHandler extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.sendRedirect("/404.html");
        response.write();
    }
}
