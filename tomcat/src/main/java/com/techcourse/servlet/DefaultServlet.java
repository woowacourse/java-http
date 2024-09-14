package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class DefaultServlet extends HttpServlet {

    private static final String RESOURCE_PATH_PREFIX = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok(RESOURCE_PATH_PREFIX + request.getUriPath());
    }
}
