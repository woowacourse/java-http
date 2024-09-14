package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class GreetingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok("Hello world!", "plain");
    }
}
