package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class GreetingServlet extends HttpServlet {

    private static final String PAGE_RESOURCE_PATH = "static/hello.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok(PAGE_RESOURCE_PATH);
    }
}
