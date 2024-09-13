package com.techcourse.servlet;

import org.apache.catalina.servlet.AbstractHttpServlet;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class IndexRedirectServlet extends AbstractHttpServlet {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.redirectTo("/index.html");
    }
}
